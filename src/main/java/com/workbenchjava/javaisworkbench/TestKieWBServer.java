package com.workbenchjava.javaisworkbench;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.commands.CallContainerCommand;
import org.kie.server.api.commands.CommandScript;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieServerCommand;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponsesList;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestKieWBServer {
    @Test
    public  void  testJson(){
        Person person=new Person();
        person.setAge(30);
        person.setName("张三");
        String url = "http://localhost:8081/kie-server/services/rest/server";
        String username = "kieserver";
        String password = "kieserver1!";

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);//请求方式
        config.setTimeout(30000L);//如果请求失败，再次请求的间隔时间

        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);//创建kie-server客户端
        RuleServicesClient rules = client.getServicesClient(RuleServicesClient.class);//创建访问规则的客户端

        KieCommands cmdFactory = KieServices.Factory.get().getCommands();

        List<Command<?>> commands = new LinkedList<>();
        commands.add(cmdFactory.newInsert(person, "person"));//输入事务，在请求获取请求时，和web请求获取一样
        commands.add(cmdFactory.newFireAllRules());
        ServiceResponse<ExecutionResults> response = rules.executeCommandsWithResults("testkieserver",cmdFactory.newBatchExecution(commands, "testKieSession"));
        //第一个参数，容器名称，第二个参数将传放的值放到容器中 testKieSession  表示KieSession 第二个参数可有可无，如果没有则使用的是kiesession默认值
        System.out.println(response.getMsg());
        ExecutionResults result = response.getResult(); //获取请求
        ServiceResponse.ResponseType type = response.getType();  //请求状态
        System.out.println(type.name());
        person = (Person) result.getValue("person"); //和web 获取前端传值很像吧
        System.out.println(person.getName());
    }

    @Test
    public  void esttssxml(){
        Person person=new Person();
        person.setAge(30);
        person.setName("张三");
        String url = "http://localhost:8081/kie-server/services/rest/server";
        String username = "kieserver";
        String password = "kieserver1!";
        InsertObjectCommand insertObjectCommand1 = new InsertObjectCommand(person, "person");//person输入事务，在请求获取请求时，和web请求获取一样
        GetObjectsCommand getObjectsCommand = new GetObjectsCommand();
        getObjectsCommand.setOutIdentifier("objects"); //输出标识符
        FireAllRulesCommand fireAllRulesCommand = new FireAllRulesCommand("RunAllRules");
        List  commands = new ArrayList<>();
        commands.add(insertObjectCommand1);
        commands.add(fireAllRulesCommand);
        commands.add(getObjectsCommand);
        BatchExecutionCommand command = new BatchExecutionCommandImpl(commands);
        String xStreamXml = BatchExecutionHelper.newXStreamMarshaller().toXML(command);//将请求内容设置成为XML
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);//登录服务器
        config.setMarshallingFormat(MarshallingFormat.XSTREAM);//请求方式
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);//获取请求
        String containerId = "testkieserver";//请求容器的名称
        KieServerCommand call = new CallContainerCommand(containerId, xStreamXml);//拼接kie-server命名
        List<KieServerCommand> cmds = Arrays.asList(call);//命名集合
        CommandScript script = new CommandScript(cmds);
        ServiceResponsesList reply = client.executeScript(script);//服务响应列表 请求服务
        for (ServiceResponse<? extends Object> r : reply.getResponses()) {
            System.out.println(r.getResult());
            if (r.getResult() != null) {
                ExecutionResultImpl result = (ExecutionResultImpl) BatchExecutionHelper.newXStreamMarshaller().fromXML((String) r.getResult());
                person = (Person) result.getResults().get("person");  //和web  results功能是一样的
                // Objects From insert(fact0) in rule. The problem is that they are staying and multiplying there in Drools, don't know yet how to manage it. ToDo.
                ArrayList<Object> objects = (ArrayList<Object>) result.getResults().get("objects");//和web  results功能是一样的
                System.out.println(objects+ person.getName());
            }
            else
                System.out.println("Empty result...?");
        }
    }

}
