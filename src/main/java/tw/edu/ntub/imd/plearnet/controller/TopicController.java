package tw.edu.ntub.imd.plearnet.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tw.edu.ntub.imd.plearnet.bean.MessageBean;
import tw.edu.ntub.imd.plearnet.bean.TagBean;
import tw.edu.ntub.imd.plearnet.bean.TopicBean;
import tw.edu.ntub.imd.plearnet.bean.UserAccountBean;
import tw.edu.ntub.imd.plearnet.databaseconfig.entity.Message;
import tw.edu.ntub.imd.plearnet.databaseconfig.entity.Topic;
import tw.edu.ntub.imd.plearnet.service.MessageService;
import tw.edu.ntub.imd.plearnet.service.TagService;
import tw.edu.ntub.imd.plearnet.service.TopicService;
import tw.edu.ntub.imd.plearnet.service.UserAccountService;
import tw.edu.ntub.imd.plearnet.util.http.BindingResultUtils;
import tw.edu.ntub.imd.plearnet.util.http.ResponseEntityBuilder;
import tw.edu.ntub.imd.plearnet.util.json.array.ArrayData;
import tw.edu.ntub.imd.plearnet.util.json.object.ObjectData;

import javax.validation.Valid;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/topic")
public class TopicController {
    private final TopicService topicService;
    private final TagService tagService;
    private final UserAccountService userAccountService;
    private final MessageService messageService;

    @GetMapping(path = "/topic")
        public ResponseEntity<String> topicGet(@RequestParam(name = "topicID") Integer topicID){
            ArrayData arrayData = new ArrayData();

            for (TopicBean topicBean : topicService.searchMessageByTopicID(topicID)) {
                ObjectData objectData = arrayData.addObject();
                objectData.add("message_id",topicBean.getMessageId());
                objectData.add("user_id", topicBean.getUserId());
                objectData.add("message_content", topicBean.getMessageContent());

                Integer userId = topicBean.getUserId();

                Optional<UserAccountBean> userAccountBeanOptional = userAccountService.getById(userId);

                userAccountBeanOptional.orElseThrow(() ->new RuntimeException("查無此用戶"));
                UserAccountBean userAccountBean = userAccountBeanOptional.get();

                objectData.add("user_name", userAccountBean.getName());
            }

            Optional<TopicBean> topicBeanOptional = topicService.getById(topicID);

            topicBeanOptional.orElseThrow(() -> new RuntimeException("查無此筆記"));
            TopicBean topicBean = topicBeanOptional.get();

            ObjectData objectData = arrayData.addObject();
            objectData.add("view", topicBean.getView());
            objectData.add("likes", topicBean.getLikes());
            objectData.add("title", topicBean.getTitle());
            objectData.add("content", topicBean.getContent());
            objectData.add("create_date", topicBean.getCreateDate());
            objectData.add("edit_date", topicBean.getEditDate());
            objectData.add("author",topicBean.getAuthor());

            Integer userId = topicBean.getAuthor();

            Optional<UserAccountBean> userAccountBeanOptional = userAccountService.getById(userId);

            userAccountBeanOptional.orElseThrow(() ->new RuntimeException("查無此用戶"));
            UserAccountBean userAccountBean = userAccountBeanOptional.get();

            objectData.add("author_name", userAccountBean.getName());

            return  ResponseEntityBuilder.success()
                    .message("查詢成功")
                    .data(arrayData)
                    .build();


        }

    @GetMapping(path = "/tagList")
    public ResponseEntity<String> tagList(@RequestParam(name = "tagType") Integer tagType){
       ArrayData arrayData = new ArrayData();

       for(TagBean tagBean : tagService.searchAll(tagType)){
           ObjectData objectData = arrayData.addObject();
           objectData.add("id",tagBean.getId());
           objectData.add("name",tagBean.getName());
       }

       return ResponseEntityBuilder.success()
               .message("查詢成功")
               .data(arrayData)
               .build();
    }

    @GetMapping(path = "/tagSearch")
    public ResponseEntity<String> tagSearch(@RequestParam(name = "tag") Integer tag){
        ArrayData arrayData = new ArrayData();

        for(TopicBean topicBean : topicService.searchAll(tag)){
            ObjectData objectData = arrayData.addObject();
            objectData.add("id",topicBean.getId());
            objectData.add("title",topicBean.getTitle());
        }

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(arrayData)
                .build();
    }

    @PostMapping(path = "/postNote")
    public ResponseEntity<String> createTopic(@Valid @RequestBody TopicBean topicBean,
                                              BindingResult bindingResult){
        BindingResultUtils.validate(bindingResult);
        topicService.save(topicBean);
        return ResponseEntityBuilder.success()
                .message("新增成功")
                .build();
    }

    @PostMapping(path = "/postMessage")
    public ResponseEntity<String> createMessage(@Valid @RequestBody MessageBean messageBean,
                                                BindingResult bindingResult){
        BindingResultUtils.validate(bindingResult);
        messageService.save(messageBean);
        return ResponseEntityBuilder.success()
                .message("留言成功")
                .build();
    }

    @PatchMapping(path = "/editNote")
    public ResponseEntity<String> updateTopic(@RequestBody TopicBean topicBean){
        topicService.update(topicBean.getId(), topicBean);
        return ResponseEntityBuilder.success()
                .message("修改成功")
                .build();
    }

}
