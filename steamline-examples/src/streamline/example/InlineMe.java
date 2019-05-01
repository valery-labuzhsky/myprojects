package streamline.example;

public class InlineMe {

    public void simpleBefore() {
        int inline = 8;

        int target = inline;
    }

    public void simpleAfter() {
        int inline = 8;

        int target = 8;
    }

    public void setBefore() {
        int inline = 8;

        inline = 7;

        int target = inline;

        inline = 9;
    }


    public void setAfter() {
        int inline = 8;

        inline = 7;

        int target = 7;

        inline = 9;
    }

    public void ifBefore() {
        int inline = 8;

        if (1==1) {
            inline = 7;
        } else {
            inline = 8;
        }

        int target = inline;

        inline = 9;
    }

    public void ifAfter() {
        int inline = 8;

        if (1==1)
            inline = 7;

        inline = 6;

        int target = inline;

        inline = 9;
    }

    public static void main(String[] args) {
    }
}
