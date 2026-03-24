package com.nileonx.leetchat_springboot.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensFilterConfig {
//    序号	方法	说明	默认值
//1	ignoreCase	忽略大小写	true
//            2	ignoreWidth	忽略半角圆角	true
//            3	ignoreNumStyle	忽略数字的写法	true
//            4	ignoreChineseStyle	忽略中文的书写格式	true
//            5	ignoreEnglishStyle	忽略英文的书写格式	true
//            6	ignoreRepeat	忽略重复词	false
//            7	enableNumCheck	是否启用数字检测。	true
//            8	enableEmailCheck	是有启用邮箱检测	true
//            9	enableUrlCheck	是否启用链接检测	true
//            10	enableWordCheck	是否启用敏感单词检测	true
//            11	numCheckLen	数字检测，自定义指定长度。	8
//            12	wordTag	词对应的标签	none
//13	charIgnore	忽略的字符	none
//14	wordResultCondition	针对匹配的敏感词额外加工，比如可以限制英文单词必须全匹配	恒为真
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(false)
                .enableNumCheck(false)
                .enableEmailCheck(false)
                .enableUrlCheck(false)
                .enableWordCheck(true)
                .numCheckLen(8)
                .wordTag(WordTags.none())
                .charIgnore(SensitiveWordCharIgnores.defaults())
                .wordResultCondition(WordResultConditions.alwaysTrue())
                .init();
    }
}
