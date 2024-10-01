//package com.cy.config;
//
//import com.ctrip.framework.apollo.model.ConfigChange;
//import com.ctrip.framework.apollo.model.ConfigChangeEvent;
//import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ApolloChangeListener  {
//    @Autowired
//    private RefreshScope refreshScope;
//    /**
//     * 使用说明
//     *   @ApolloConfigChangeListener(value = {"application.yml","user"}, interestedKeyPrefixes = {"spring.","my.config"})：
//     *   当在Apollo配置中心，修改application.yml或user.properties下的，前缀为spring.或my.config的属性时，触发刷新方法执行。
//     * @param changeEvent
//     */
//    @ApolloConfigChangeListener(value = {"application.yml","application"})
//    public void refresh(ConfigChangeEvent changeEvent) {
//        System.out.println("Changes for namespace " + changeEvent.getNamespace());
//        for(String key : changeEvent.changedKeys()) {
//            ConfigChange change = changeEvent.getChange(key);
//            System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
//        }
//        // 刷新的bean 所有 带@RefreshScope 的类
//        refreshScope.refreshAll();
//        // 刷新指定的bean
////        refreshScope.refresh("myConfigProperties");
//    }
//}