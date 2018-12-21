package com.workbenchjava.javaisworkbench;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:Spring.xml"})
public class TestSpring {
    @KSession("testKieSession")//注： 这里的值与配置文件中的值是一样的
    KieSession kSession;

    @Test
    public void runRules() {
        while (true) {
            try {
                Person p = new Person();
                p.setAge(30);
                p.setName("张三");
                kSession.insert(p);
                int i = kSession.fireAllRules();
                System.out.print("Java调用Workbench 自动扫描，共执行了" + i + "条规则");
                System.out.println("修改后的结果" + p.getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }
}
