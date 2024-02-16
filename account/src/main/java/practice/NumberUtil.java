package practice;

import lombok.experimental.UtilityClass;

//UtilityClass -> static 한 클래스를 만들어 놓고 사용하는 것을 의미
//final을 자동으로 형성을 해준다
//본연의 기능만 사용할 수 있도록 -> 잘 막아준다.
@UtilityClass
public class NumberUtil {

    public static Integer sum (Integer a, Integer b){
        return  a + b;
    }

    public static Integer minus (Integer a, Integer b){
        return  a - b;
    }
}
