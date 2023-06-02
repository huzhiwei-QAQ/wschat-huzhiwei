import java.util.Arrays;

public class Pojo {
    private String name;
    private String age;

    public void say(){

//        System.out.println(1/0);
//        try {
//            System.out.println(1/0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }



}
