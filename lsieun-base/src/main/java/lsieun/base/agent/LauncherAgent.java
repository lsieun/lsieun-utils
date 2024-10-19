package lsieun.base.agent;

//import lsieun.utils.core.operators.logical.LogicAsmUtils;

import java.lang.instrument.Instrumentation;

public class LauncherAgent {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        String msg = String.format("[LauncherAgent::agentmain] inst = %s", inst);
        System.out.println(msg);

//        Module src = Object.class.getModule();
//        Module target = LogicAsmUtils.class.getModule();
//
//        // prepare extra reads
//        Set<Module> extraReads = Collections.singleton(target);
//
//        // prepare extra exports
//        Set<String> packages = src.getPackages();
//        Map<String, Set<Module>> extraExports = new HashMap<>();
//        for (String pkg : packages) {
//            extraExports.put(pkg, extraReads);
//        }
//
//        // prepare extra opens
//        Map<String, Set<Module>> extraOpens = new HashMap<>();
//        for (String pkg : packages) {
//            extraOpens.put(pkg, extraReads);
//        }
//
//        // prepare extra uses
//        Set<Class<?>> extraUses = Collections.emptySet();
//
//        // prepare extra provides
//        Map<Class<?>, List<Class<?>>> extraProvides = Collections.emptyMap();
//
//        // redefine module
//        inst.redefineModule(src, extraReads, extraExports, extraOpens, extraUses, extraProvides);
//        String exitMsg = String.format("[LauncherAgent::agentmain] - %s ~ %s", src, target);
//        System.out.println(exitMsg);
    }
}