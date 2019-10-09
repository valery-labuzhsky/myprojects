package statref.model.builder;

import statref.model.SElement;
import statref.model.fragment.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;

public class BFactory {
    // TODO the key thing for know is naming consistency, this way I can create registry in automated way

    public static Fragment builder(Fragment f) {
        return builder((SElement) f);
    }

    public static <E extends SElement> E builder(E element) {
        HashSet<Class> ss = new HashSet<>();
        Class<?> clazz = element.getClass();
        while (clazz!=null) {
            for (Class<?> intf : clazz.getInterfaces()) {
                if (SElement.class.isAssignableFrom(intf)) {
                    ss.add(intf);
                }
            }
            clazz = clazz.getSuperclass();
        }
        HashSet<Class> max = new HashSet<>();
        for (Class<?> intf : ss) {
            boolean add = true;
            for (Iterator<Class> iterator = max.iterator(); iterator.hasNext(); ) {
                Class<?> m = iterator.next();
                if (m.isAssignableFrom(intf)) {
                    iterator.remove();
                } else if (intf.isAssignableFrom(m)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                max.add(intf);
            }
        }
        if (max.isEmpty()) {
            throw new RuntimeException("No model interface is found for "+element.getClass().getName());
        } else if (max.size()>1) {
            throw new RuntimeException("Many interfaces are found for "+element.getClass().getName()+": "+max);
        }
        Class model = max.iterator().next();
        String bname = model.getName().replaceFirst("statref.model.(.*).S", "statref.model.builder.$1.B");
        try {
            Class<?> bclass = Class.forName(bname);
            Constructor<?> constructor = bclass.getConstructor(model);
            return (E) constructor.newInstance(element);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
