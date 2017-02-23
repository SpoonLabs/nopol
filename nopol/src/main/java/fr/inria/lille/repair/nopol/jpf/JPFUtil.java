package fr.inria.lille.repair.nopol.jpf;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

public class JPFUtil {

    public static Config createConfig(String[] args, String qualifiedNameClass, String classpath, String sourcePath) {
        Config conf = JPF.createConfig(args);


        conf.setProperty("report.console.show_steps", "true");
        conf.setProperty("report.console.constraint", "constraint,snapshot");
        conf.setProperty("report.console.class", "gov.nasa.jpf.report.ConsolePublisher");
        conf.setProperty("report.class", "gov.nasa.jpf.report.Reporter");
        conf.setProperty("report.xml.class", "gov.nasa.jpf.report.XMLPublisher");
        conf.setProperty("report.console.probe", "statistics");
        conf.setProperty("report.console.show_code", "false");
        conf.setProperty("report.console.transition", "search.properties,gov.nasa.jpf.vm.NotDeadlockedProperty,gov.nasa.jpf.vm.NoUncaughtExceptionsProperty");
        conf.setProperty("report.html.class", "gov.nasa.jpf.report.HTMLPublisher");
        conf.setProperty("report.html.property_violation", "test.report.console.finished,result");
        conf.setProperty("report.html.constraint", "constraint");
        conf.setProperty("report.console.show_method", "true");
        conf.setProperty("report.console.start", "jpf,sut");
        conf.setProperty("report.html.finished", "result,statistics,error,snapshot,output");
        conf.setProperty("report.html.start", "jpf,sut,platform,user,dtg,config");
        conf.setProperty("report.console.property_violation", "error,snapshot");
        conf.setProperty("report.console.finished", "result,statistics");
        conf.setProperty("report.publisher", "console");

        conf.setProperty("log.level", "warning");
        conf.setProperty("log.handler.class", "gov.nasa.jpf.util.LogHandler");

        conf.setProperty("cg.break_single_choice", "false");
        conf.setProperty("cg.threads.break_arrays", "false");
        conf.setProperty("cg.threads.break_start", "true");
        conf.setProperty("cg.enable_atomic", "true");
        conf.setProperty("cg.max_processors", "1");
        conf.setProperty("cg.seed", "42");
        conf.setProperty("cg.enumerate_random", "false");
        conf.setProperty("cg.boolean.false_first", "true");
        conf.setProperty("cg.threads.break_yield", "true");
        conf.setProperty("cg.randomize_choices", "NONE");
        conf.setProperty("cg.threads.break_sleep", "true");

        conf.setProperty("race.exclude", "java.*,javax.*");

        conf.setProperty("listener.autoload", "gov.nasa.jpf.NonNull,gov.nasa.jpf.Const");
        conf.setProperty("listener.gov.nasa.jpf.Const", "gov.nasa.jpf.tools.ConstChecker");
        conf.setProperty("listener.gov.nasa.jpf.NonNull", "gov.nasa.jpf.tools.NonNullChecker");


        conf.setProperty("search.heuristic.queue_limit", "-1");
        conf.setProperty("search.multiple_errors", "false");
        conf.setProperty("search.match_depth", "false");
        conf.setProperty("search.min_free", "1M");

        conf.setProperty("peer_packages", "gov.nasa.jpf.symbc;,gov.nasa.jpf.symbc;,gov.nasa.jpf.vm,<model>,<default>");

        conf.setProperty("jvm.insn_factory.class", "gov.nasa.jpf.symbc.SymbolicInstructionFactory");

        conf.setProperty("vm.serializer.class", "gov.nasa.jpf.vm.serialize.CFSerializer");
        conf.setProperty("vm.shared.break_on_exposure", "true");
        conf.setProperty("vm.scheduler.sync.class", "gov.nasa.jpf.vm.AllRunnablesSyncPolicy");
        conf.setProperty("vm.scheduler_factory.class", "gov.nasa.jpf.vm.DefaultSchedulerFactory");
        conf.setProperty("vm.shared.skip_constructed_finals", "false");
        conf.setProperty("vm.shared.never_break_methods", "java.util.concurrent.ThreadPoolExecutor.processWorkerExit,java.util.concurrent.locks.Abstract*Synchronizer.*,java.util.concurrent.ThreadPoolExecutor.getTask,java.util.concurrent.atomic.Atomic*.*,java.util.concurrent.Exchanger.doExchange,java.util.concurrent.ThreadPoolExecutor.interruptIdleWorkers");
        conf.setProperty("vm.store_steps", "false");
        conf.setProperty("vm.shared.sync_detection", "true");
        conf.setProperty("vm.boot_classpath", "<system>");
        conf.setProperty("vm.time.class", "gov.nasa.jpf.vm.SystemTime");
        conf.setProperty("vm.verify.ignore_path", "true");
        conf.setProperty("vm.scheduler.class", "gov.nasa.jpf.vm.DelegatingScheduler");
        conf.setProperty("vm.max_alloc_gc", "-1");
        conf.setProperty("vm.shared.skip_finals", "true");
        conf.setProperty("vm.sysprop.source", "SELECTED");
        conf.setProperty("vm.heap.class", "gov.nasa.jpf.vm.OVHeap");
        conf.setProperty("vm.process_finalizers", "false");
        conf.setProperty("vm.class", "gov.nasa.jpf.vm.SingleProcessVM");
        conf.setProperty("vm.finalize", "false");
        conf.setProperty("vm.pass_uncaught_handler", "true");
        conf.setProperty("vm.backtracker.class", "gov.nasa.jpf.vm.DefaultBacktracker");
        conf.setProperty("vm.scheduler.sharedness.class", "gov.nasa.jpf.vm.PathSharednessPolicy");
        conf.setProperty("vm.shared.never_break_fields", "java.lang.Thread*.*,java.lang.System.*,java.lang.Runtime.*,java.lang.Boolean.*,java.lang.String.*,java.lang.*.TYPE,java.util.HashMap.EMPTY_TABLE,java.util.HashSet.PRESENT,java.util.concurrent.ThreadPoolExecutor*.*,java.util.concurrent.ThreadPoolExecutor*.*,java.util.concurrent.TimeUnit.*,java.util.concurrent.Exchanger.CANCEL,java.util.concurrent.Exchanger.NULL_ITEM,java.util.concurrent.Executors$DefaultThreadFactory.*,sun.misc.VM.*,sun.misc.SharedSecrets.*,sun.misc.Unsafe.theUnsafe,gov.nasa.jpf.util.test.TestJPF.*");
        conf.setProperty("vm.untracked", "true");
        conf.setProperty("vm.classloader.class", "gov.nasa.jpf.jvm.JVMSystemClassLoaderInfo");
        conf.setProperty("vm.statics.class", "gov.nasa.jpf.vm.OVStatics");
        conf.setProperty("vm.path_output", "false");
        conf.setProperty("vm.ignore_uncaught_handler", "false");
        conf.setProperty("vm.threadlist.class", "gov.nasa.jpf.vm.ThreadList");
        conf.setProperty("vm.reuse_tid", "false");
        conf.setProperty("vm.fields_factory.class", "gov.nasa.jpf.vm.DefaultFieldsFactory");
        conf.setProperty("vm.restorer.class", ".vm.DefaultMementoRestorer");
        conf.setProperty("vm.gc", "true");
        conf.setProperty("vm.no_orphan_methods", "false");
        conf.setProperty("vm.max_transition_length", "50000");
        conf.setProperty("vm.shared.skip_static_finals", "false");
        conf.setProperty("vm.tree_output", "true");

        conf.setProperty("symbolic.max_int", "100");
        conf.setProperty("symbolic.min_int", "-100");
        conf.setProperty("symbolic.dp", "z3");

        conf.setProperty("search.class", "gov.nasa.jpf.search.DFSearch");
        conf.setProperty("search.heuristic.branch.count_early", "true");
        conf.setProperty("search.heuristic.branch.no_branch_return", "-1");

        conf.setProperty("sourcepath", sourcePath);
        conf.setProperty("classpath", classpath);
        conf.setProperty("target", qualifiedNameClass);

        return conf;
    }
}
