package com.workbenchjava.javaisworkbench;

import org.drools.core.io.impl.UrlResource;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.io.InputStream;

public class Testworkbench {
    @Test
    public void test() throws IOException {
        String url = "http://localhost:8081/kie-drools-wb/maven2/com/workbenchjava/javaIsWorkbench/1.0.0/javaIsWorkbench-1.0.0.jar";
        KieServices ks = KieServices.Factory.get();
        UrlResource urlResource = (UrlResource) ks.getResources().newUrlResource(url);
        urlResource.setUsername("kie");
        urlResource.setPassword("kie");
        urlResource.setBasicAuthentication("enabled");
        InputStream is = urlResource.getInputStream();
        KieRepository kr = ks.getRepository();
        KieModule kModule = kr.addKieModule(ks.getResources().newInputStreamResource(is));
        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
        KieSession kieSession = kContainer.newKieSession();
        Person p = new Person();
        p.setAge(30);
        p.setName("张三");
        kieSession.insert(p);
        int i = kieSession.fireAllRules();
        System.out.print("Java调用Workbench，共执行了" + i + "条规则");
        System.out.print("修改后的结果" + p.getName());
    }


    @Test
    public void runRules2() {
        KieServices kieServices = KieServices.Factory.get();
        ReleaseId releaseId = kieServices.newReleaseId("com.workbenchjava", "javaIsWorkbench", "1.0.0");
        KieContainer kContainer = kieServices.newKieContainer(releaseId);
        KieSession kSession = kContainer.newKieSession();
        Person p = new Person();
        p.setAge(30);
        p.setName("张三");
        kSession.insert(p);
        int i = kSession.fireAllRules();
        System.out.print("Java调用Workbench，共执行了" + i + "条规则");
        System.out.print("修改后的结果" + p.getName());
    }

    @Test
    public void runkScanner() {
        KieServices kieServices = KieServices.Factory.get();
        ReleaseId releaseId = kieServices.newReleaseId("com.workbenchjava", "javaIsWorkbench", "1.0.0");
        KieContainer kContainer = kieServices.newKieContainer(releaseId);
        KieScanner kScanner = kieServices.newKieScanner(kContainer);
        // 启动KieScanner轮询Maven存储库每10秒
        kScanner.start(10000L);
        while (true) {
            try {
                KieSession kSession = kContainer.newKieSession();
                Person p = new Person();
                p.setAge(30);
                p.setName("张三");
                kSession.insert(p);
                int i = kSession.fireAllRules();
                System.out.print("Java调用Workbench，自动扫描 共执行了" + i + "条规则");
                System.out.println("修改后的结果" + p.getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
