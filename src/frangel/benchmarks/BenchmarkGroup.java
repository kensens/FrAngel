package frangel.benchmarks;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import frangel.SynthesisTask;

public enum BenchmarkGroup {
    SYPET,
    GEOMETRY,
    CONTROL_STRUCTURES,
    GITHUB,
    OTHER;

    private static final Set<TaskCreator> ALL_CREATORS = new HashSet<>();
    private final List<TaskCreator> creators = new ArrayList<>();

    static {
        Class<?> baseCreator = TaskCreator.class;
        try {
            for (final ClassInfo info : ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("frangel.benchmarks")) {
                Class<?> cls = info.load();
                if (baseCreator.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()))
                    Class.forName(cls.getName());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SynthesisTask getTask(String name) {
        for (TaskCreator creator : ALL_CREATORS) {
            SynthesisTask task = creator.createTask();
            if (task.getName().equals(name))
                return task;
        }
        return null;
    }

    public static Set<TaskCreator> getAllCreators() {
        return ALL_CREATORS;
    }

    public static BenchmarkGroup findGroup(TaskCreator creator) {
        for (BenchmarkGroup group : values())
            if (group.creators.contains(creator))
                return group;
        return null;
    }

    public void register(TaskCreator creator) {
        creators.add(creator);
//        SynthesisTask task = creator.createTask();
//        if (!task.getName().replace("_", "").equalsIgnoreCase(creator.getClass().getSimpleName().replace("_", "")))
//            System.err.println("Creator name: " + creator.getClass().getSimpleName() + ", task name: " + task.getName());
        ALL_CREATORS.add(creator);
    }

    public List<SynthesisTask> getTasks() {
        creators.sort((TaskCreator c1, TaskCreator c2) -> c1.getClass().getName().compareTo(c2.getClass().getName()));
        List<SynthesisTask> tasks = new ArrayList<>();
        for (TaskCreator creator : creators) {
            SynthesisTask task = creator.createTask();
            task.setGroup(this.toString());
            tasks.add(task);
        }
        return tasks;
    }

    public List<Class<?>> getCreatorClasses() {
        creators.sort((TaskCreator c1, TaskCreator c2) -> c1.getClass().getName().compareTo(c2.getClass().getName()));
        List<Class<?>> list = new ArrayList<>();
        for (TaskCreator creator : creators)
            list.add(creator.getClass());
        return list;
    }
}
