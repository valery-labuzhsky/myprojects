package statref.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 02/01/18.
 *
 * @author ptasha
 */
@Retention(RetentionPolicy.CLASS)
public @interface StatRefs {
    Class<?>[] value();
}
