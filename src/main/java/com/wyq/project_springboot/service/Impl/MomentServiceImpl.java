package com.wyq.project_springboot.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.comment.CommentDTO;
import com.wyq.project_springboot.dto.moment.MomentDTO;
import com.wyq.project_springboot.entity.*;
import com.wyq.project_springboot.mapper.*;
import com.wyq.project_springboot.service.MomentService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.wyq.project_springboot.utils.ExpConstants.*;
import static com.wyq.project_springboot.utils.ImageUploadConstant.*;
import static com.wyq.project_springboot.utils.RedisConstants.*;

@Service
@Transactional
public class MomentServiceImpl implements MomentService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MomentMapper momentMapper;
    @Autowired
    private MomentImageMapper momentImageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConcernMapper concernMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentImageMapper commentImageMapper;
    @Autowired
    private CircleMapper circleMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result insertMoment(String title, String content, int forwardMomentId, MultipartFile[] images, int circleId) throws IOException {

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setTitle(title);
        moment.setContent(content);
        moment.setCircleId(circleId);
        //判断该动态是否为转发动态
        if (forwardMomentId != 0) {
            moment.setForwardMomentId(forwardMomentId);
        }
        //向数据库插入动态，并返回动态ID主键给moment对象
        momentMapper.insertMoment(moment);

        //如果该动态不是转发动态，则可以添加图片
        if (forwardMomentId != 0) {
            if (images != null) {

                for (MultipartFile image : images) {
                    //获取原始文件名
                    String fileName = image.getOriginalFilename();
                    //获取文件后缀
                    String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
                    //使用UUID获取不重复的随机文件名
                    fileName = UUID.randomUUID().toString() + fileSuffix;

                    //将图片文件保存到文件目录下
                    String fileRelativePath = MOMENT_IMAGE_PREFIX + fileName;
                    String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
                    image.transferTo(new File(finalAbsolutePath));

                    //创建图片对象
                    Image imageToMoment = new Image();
                    imageToMoment.setDependId(moment.getId());
                    imageToMoment.setImagePath(fileRelativePath);
                    imageToMoment.setImageSize(image.getSize());

                    //向数据库插入图片
                    momentImageMapper.insertImage(imageToMoment);
                }
            }
        }

        //向Redis中动态点赞榜和评论榜插入该动态
        stringRedisTemplate.opsForZSet().add(MOMENT_THUMBS_UP_RANK_KEY, Integer.toString(moment.getId()),0);
        stringRedisTemplate.opsForZSet().add(MOMENT_COMMENT_RANK_KEY, Integer.toString(moment.getId()),0);

        //判断用户今日发布动态增加经验次数是否已达上限
        Double score = stringRedisTemplate.opsForZSet().score(EXP_TASK_KEY + userId, SEND_MOMENT_TASK_KEY);
        if(score == null || score < SEND_MOMENT_TASK_TOTAL){
            //为用户增加经验
            userMapper.updateExperience(userId, SEND_MOMENT_EXP);
            //为Redis中用户任务完成情况该操作次数+1
            stringRedisTemplate.opsForZSet().incrementScore(EXP_TASK_KEY + userId, SEND_MOMENT_TASK_KEY, 1);
        }

        //向该用户的粉丝推送该动态
        List<Concern> concerns = concernMapper.selectBeConcernedList(userId);
        for(Concern concern : concerns){
            int fansId = concern.getUserId();
            String key = CONCERN_KEY + fansId;
            stringRedisTemplate.opsForZSet().add(key,Integer.toString(moment.getId()),System.currentTimeMillis());
        }

        return Result.success();
    }

    @Override
    public Result deleteMoment(int momentId) {

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        momentMapper.deleteMoment(userId, momentId);
        momentImageMapper.deleteMomentImage(momentId);

        String key = MOMENT_KEY + momentId;
        //删除redis中该动态
        stringRedisTemplate.delete(key);
        //删除该动态点赞榜
        stringRedisTemplate.delete(MOMENT_THUMBS_UP_KEY + momentId);
        //删除点赞排行榜中该动态
        stringRedisTemplate.opsForZSet().remove(MOMENT_THUMBS_UP_RANK_KEY, key);
        //删除该动态评论榜
        stringRedisTemplate.delete(MOMENT_COMMENT_KEY + momentId);
        //删除评论排行榜中该动态
        stringRedisTemplate.opsForZSet().remove(MOMENT_COMMENT_RANK_KEY, key);
        return Result.success();
    }

    @Override
    public Result getMomentList(int userId, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int ownId = (Integer) userMap.get("id");

        //使用pageHelper进行倒叙、分页查询
        PageHelper.startPage(pageNum, pageSize, "record_time desc");

        Page<Moment> page = (Page<Moment>) momentMapper.selectUserMomentList(userId);

        //为momentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为momentListDTO的momentDTOList赋值
        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : page.getResult()) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }
        listDTO.setListData(momentDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getPopularMomentList(String lastMark,int offset,int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //从Redis中获取点赞榜中数据
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(MOMENT_COMMENT_RANK_KEY, 0, Double.parseDouble(lastMark), offset, pageSize);
        if(typedTuples == null || typedTuples.isEmpty()){
            return Result.success();
        }

        //moment的id集合
        List<Integer> momentIdList = new ArrayList<>(pageSize);

        int lastMarkCount = 0;
        int offsetCount = 1;
        for(ZSetOperations.TypedTuple<String> typedTuple : typedTuples){
            //向id集合中添加该数据
            momentIdList.add(Integer.parseInt(typedTuple.getValue()));

            //计算偏移量
            int score = typedTuple.getScore().intValue();

            if(score == lastMarkCount){
                offsetCount++;
            }else {
                //得到最后一个元素标识
                lastMarkCount = score;
                offsetCount = 1;
            }
        }

        //设置最后一个元素标识和偏移量
        listDTO.setLastMark(Integer.toString(lastMarkCount));
        listDTO.setOffset(offsetCount);

        //根据id集合查询动态
        List<Moment> momentList = momentMapper.selectMomentByIdList(momentIdList);

        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : momentList) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }

        listDTO.setListData(momentDTOList);
        return Result.success(listDTO);
    }

    @Override
    public Result getMoment(int momentId) throws JsonProcessingException {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //根据momentId到redis查询
        String key = MOMENT_KEY + momentId;
        String momentJSON = stringRedisTemplate.opsForValue().get(key);

        MomentDTO momentDTO = new MomentDTO();

        //判断redis中该id动态是否存在
        if (momentJSON != null) {
            Moment moment = objectMapper.readValue(momentJSON, Moment.class);

            //将moment转为momentDTO
            momentDTO = momentToMomentDTO(moment, userId);

            return Result.success(momentDTO);

        }

        Moment moment = momentMapper.selectMoment(momentId);
        if (moment == null) {
            return Result.error("动态不存在");
        }

        //如果动态存在于数据库中，则缓存入redis
        stringRedisTemplate.opsForValue().set(MOMENT_KEY + momentId, objectMapper.writeValueAsString(moment), MOMENT_TTL, TimeUnit.MINUTES);

        //因为存入redis中的momentDTO不需要包含用户是否点赞的信息，所以不调用自己定义的转换DTO的方法
        momentDTO.setMoment(moment);

        User user = userMapper.selectUserById(moment.getUserId());
        //将手机号码去除
        user.setPhoneNumber(null);

        momentDTO.setUser(user);

        return Result.success(momentDTO);
    }

    public Result getRecommendMomentList(String lastMark,int offset,int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //从Redis中获取点赞榜中数据
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(MOMENT_THUMBS_UP_RANK_KEY, 0, Double.parseDouble(lastMark), offset, pageSize);
        if(typedTuples == null || typedTuples.isEmpty()){
            return Result.success();
        }

        //moment的id集合
        List<Integer> momentIdList = new ArrayList<>(pageSize);

        int lastMarkCount = 0;
        int offsetCount = 1;
        for(ZSetOperations.TypedTuple<String> typedTuple : typedTuples){
            //向id集合中添加该数据
            momentIdList.add(Integer.parseInt(typedTuple.getValue()));

            //计算偏移量
            int score = typedTuple.getScore().intValue();

            if(score == lastMarkCount){
                offsetCount++;
            }else {
                //得到最后一个元素标识
                lastMarkCount = score;
                offsetCount = 1;
            }
        }

        //设置最后一个元素标识和偏移量
        listDTO.setLastMark(Integer.toString(lastMarkCount));
        listDTO.setOffset(offsetCount);

        //根据id集合查询动态
        List<Moment> momentList = momentMapper.selectMomentByIdList(momentIdList);

        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : momentList) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }

        listDTO.setListData(momentDTOList);
        return Result.success(listDTO);
    }

    public Result getConcernUserMomentList(String lastMark,int offset,int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        String key = CONCERN_KEY + userId;

        //从Redis中获取关注收件箱中数据
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, Double.parseDouble(lastMark), offset, pageSize);
        if(typedTuples == null || typedTuples.isEmpty()){
            return Result.success();
        }

        //moment的id集合
        List<Integer> momentIdList = new ArrayList<>(pageSize);

        long lastMarkCount = 0;
        int offsetCount = 1;
        for(ZSetOperations.TypedTuple<String> typedTuple : typedTuples){
            //向id集合中添加该数据
            momentIdList.add(Integer.parseInt(typedTuple.getValue()));

            //计算偏移量
            long score = typedTuple.getScore().intValue();

            if(score == lastMarkCount){
                offsetCount++;
            }else {
                //得到最后一个元素标识
                lastMarkCount = score;
                offsetCount = 1;
            }
        }

        //设置最后一个元素标识和偏移量
        listDTO.setLastMark(Long.toString(lastMarkCount));
        listDTO.setOffset(offsetCount);

        //根据id集合查询动态
        List<Moment> momentList = momentMapper.selectMomentByIdList(momentIdList);

        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : momentList) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }

        listDTO.setListData(momentDTOList);
        return Result.success(listDTO);
    }

    @Override
    public Result addMomentThumbsUp(int momentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        // TODO 判断动态是否存在


        String key = MOMENT_THUMBS_UP_KEY + momentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        //如果未点赞
        if (score == null) {
            //为数据库中动态点赞数+1
            int i = momentMapper.addMomentThumbsUpCount(momentId);
            if (i > 0) {
                //为redis中动态的点赞集添加用户id
                stringRedisTemplate.opsForZSet().add(key, Integer.toString(userId), System.currentTimeMillis());
                //为点赞榜中该动态的分数+1
                stringRedisTemplate.opsForZSet().incrementScore(MOMENT_THUMBS_UP_RANK_KEY, Integer.toString(momentId), 1);
            }
            return Result.success();
        }
        return Result.error("已点赞过该动态");
    }

    @Override
    public Result subMomentThumbsUp(int momentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");


        // TODO 判断动态是否存在


        String key = MOMENT_THUMBS_UP_KEY + momentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        //如果已点赞
        if (score != null) {
            //为数据库中动态点赞数+1
            int i = momentMapper.subMomentThumbsUpCount(momentId);
            if (i > 0) {
                //为redis中动态的点赞集添加用户ID
                stringRedisTemplate.opsForZSet().remove(key, Integer.toString(userId));
                //为点赞榜中该动态的分数-1
                stringRedisTemplate.opsForZSet().incrementScore(MOMENT_THUMBS_UP_RANK_KEY, Integer.toString(momentId), -1);
            }
            return Result.success();
        }
        return Result.error("没有点赞过该动态");

    }


    @Override
    public Result getMomentThumbsUpState(int momentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        // TODO 判断动态是否存在

        String key = MOMENT_THUMBS_UP_KEY + momentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        if (score == null) {
            return Result.success(false);
        } else {
            return Result.success(true);
        }
    }

    @Override
    public Result insertComment(Comment comment, MultipartFile[] images) throws IOException {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        comment.setUserId(userId);

        //向数据库插入评论，并返回动态ID主键给comment对象
        commentMapper.insertComment(comment);
        momentMapper.addMomentCommentCount(comment.getMomentId());

        if (images != null) {

            for (MultipartFile image : images) {
                //获取原始文件名
                String fileName = image.getOriginalFilename();
                //获取文件后缀
                String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
                //使用UUID获取不重复的随机文件名
                fileName = UUID.randomUUID().toString() + fileSuffix;

                //将图片文件保存到文件目录下
                String fileRelativePath = COMMENT_IMAGE_PREFIX + fileName;
                String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
                image.transferTo(new File(finalAbsolutePath));

                //创建图片对象
                Image imageToComment = new Image();
                imageToComment.setDependId(comment.getId());
                imageToComment.setImagePath(fileRelativePath);
                imageToComment.setImageSize(image.getSize());

                //向数据库插入图片
                commentImageMapper.insertCommentImage(imageToComment);
            }
        }

        //为redis中moment的评论集添加该评论
        stringRedisTemplate.opsForZSet().add(MOMENT_COMMENT_KEY + comment.getMomentId(), Integer.toString(comment.getId()), System.currentTimeMillis());
        //为动态评论榜中该动态的分数+1
        stringRedisTemplate.opsForZSet().incrementScore(MOMENT_COMMENT_RANK_KEY, Integer.toString(comment.getMomentId()), +1);

        //判断用户今日发送评论增加经验次数是否已达上限
        Double score = stringRedisTemplate.opsForZSet().score(EXP_TASK_KEY + userId, SEND_COMMENT_TASK_KEY);
        if(score == null || score < SEND_COMMENT_TASK_TOTAL){
            //为用户增加经验
            userMapper.updateExperience(userId, SEND_COMMENT_EXP);
            //为Redis中用户任务完成情况该操作次数+1
            stringRedisTemplate.opsForZSet().incrementScore(EXP_TASK_KEY + userId, SEND_COMMENT_TASK_KEY, 1);
        }

        return Result.success();
    }

    @Override
    public Result deleteComment(int commentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        Comment comment = commentMapper.selectComment(commentId);

        commentMapper.deleteComment(userId, commentId);
        commentImageMapper.deleteCommentImage(commentId);
        momentMapper.subMomentCommentCount(comment.getMomentId());

        //删除redis中moment的评论集中的该评论
        stringRedisTemplate.opsForZSet().remove(MOMENT_COMMENT_KEY + comment.getMomentId(), Integer.toString(comment.getId()));

        return Result.success();
    }

    @Override
    public Result getCommentList(int momentId, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //使用pageHelper进行分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Comment> page = (Page<Comment>) commentMapper.selectCommentList(momentId);

        //为commentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为commentListDTO的commentDTOList赋值
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (Comment comment : page.getResult()) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComment(comment);

            User user = userMapper.selectUserById(comment.getUserId());

            //将手机号码去除
            user.setPassword(null);
            commentDTO.setUser(user);


            String key = COMMENT_THUMBS_UP_KEY + comment.getId();
            //查询用户是否已经点赞该评论
            Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
            if (score == null) {
                commentDTO.setThumbsUp(false);
            } else {
                commentDTO.setThumbsUp(true);
            }

            commentDTOList.add(commentDTO);
        }
        listDTO.setListData(commentDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result addCommentThumbsUp(int commentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        // TODO 判断评论是否存在

        String key = COMMENT_THUMBS_UP_KEY + commentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        //如果未点赞
        if (score == null) {
            //为数据库中评论点赞数+1
            int i = commentMapper.addCommentThumbsUpCount(commentId);
            if (i > 0) {
                //为redis中评论的点赞集添加用户id
                stringRedisTemplate.opsForZSet().add(key, Integer.toString(userId), System.currentTimeMillis());
            }
            return Result.success();
        }
        return Result.error("已点赞过该评论");
    }

    @Override
    public Result subCommentThumbsUp(int commentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        // TODO 判断动态是否存在

        String key = COMMENT_THUMBS_UP_KEY + commentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        //如果已点赞
        if (score != null) {
            //为数据库中评论点赞数+1
            int i = commentMapper.subCommentThumbsUpCount(commentId);
            if (i > 0) {
                //为redis中评论的点赞集添加用户ID
                stringRedisTemplate.opsForZSet().remove(key, Integer.toString(userId));
            }
            return Result.success();
        }
        return Result.error("没有点赞过该评论");
    }

    @Override
    public Result getCommentThumbsUpState(int commentId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        // TODO 判断评论是否存在

        String key = COMMENT_THUMBS_UP_KEY + commentId;
        //查询是否已经点赞
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        if (score == null) {
            return Result.success(false);
        } else {
            return Result.success(true);
        }
    }

    @Override
    public Result searchMomentList(String searchValue, int pageNum, int pageSize) {

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        ListDTO listDTO = new ListDTO();

        //该对象作为搜索条件
        Moment selectMoment = new Moment();
        selectMoment.setTitle(searchValue);
        selectMoment.setContent(searchValue);

        PageHelper.startPage(pageNum, pageSize);
        Page<Moment> page = (Page<Moment>) momentMapper.selectMomentListByCriteria(selectMoment);

        //为momentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为momentListDTO的momentDTOList赋值
        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : page.getResult()) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }

        listDTO.setListData(momentDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getCircleMomentList(Integer circleId, Integer pageNum, Integer pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //使用pageHelper进行倒叙、分页查询
        PageHelper.startPage(pageNum, pageSize, "record_time desc");

        Page<Moment> page = (Page<Moment>) momentMapper.selectCircleMomentList(circleId);

        //为momentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为momentListDTO的momentDTOList赋值
        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : page.getResult()) {
            //将moment转为momentDTO
            MomentDTO momentDTO = momentToMomentDTO(moment, userId);
            momentDTOList.add(momentDTO);
        }
        listDTO.setListData(momentDTOList);

        return Result.success(listDTO);
    }

    private MomentDTO momentToMomentDTO(Moment moment, int userId) {
        MomentDTO momentDTO = new MomentDTO();
        momentDTO.setMoment(moment);

        User user = userMapper.selectUserById(moment.getUserId());

        //将手机号码去除
        user.setPassword(null);

        momentDTO.setUser(user);

        String key = MOMENT_THUMBS_UP_KEY + moment.getId();
        //查询用户是否已经点赞该动态
        Double score = stringRedisTemplate.opsForZSet().score(key, Integer.toString(userId));
        if (score == null) {
            momentDTO.setThumbsUp(false);
        } else {
            momentDTO.setThumbsUp(true);
        }

        //添加转发的动态
        if (moment.getForwardMomentId() != 0) {
            Moment forwardMoment = momentMapper.selectMoment(moment.getForwardMomentId());

            //如果转发动态查询到不为空，则添加进momentDTO；但如果查询到为空，则证明被转发的动态已经删除，则创建一个默认的moment添加进momentDTO中的forwardMoment
            if (forwardMoment != null) {
                momentDTO.setForwardMoment(forwardMoment);
            } else {
                momentDTO.setForwardMoment(new Moment());
            }

        }
        //添加圈子信息
        Circle circle = circleMapper.selectCircle(moment.getCircleId());
        if (circle != null) {
            momentDTO.setCircle(circle);
        }

        return momentDTO;
    }

}
