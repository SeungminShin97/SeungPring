package org.example.framework.exception.todo;


import java.util.Arrays;

/**
 * 구현되지 않은 기능(미구현 영역)을 명확히 표시하기 위한 런타임 예외.
 * <p>
 * 하나의 예외에 여러 {@link TODOCode} 값을 함께 전달할 수 있어,
 * 특정 위치에서 어떤 기능들이 아직 미완성인지 추적하는 용도로 사용한다.
 */
public class TODOException extends RuntimeException {

    private final TODOCode[] toDoCode;

    public TODOException(TODOCode... args) {
        super("ToDo");
        this.toDoCode = args;
    }

    @Override
    public String getMessage() {
        return "ToDo Error: " + Arrays.toString(toDoCode);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + Arrays.toString(toDoCode);
    }

    public TODOCode[] getToDoCodes() {
        return this.toDoCode;
    }
}
