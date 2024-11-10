package ru.exchange.model;

public class Currensy {
    private int id;
    private String code;
    private String sign;
    private String fullName;

    public Currensy(String code, String sign, String fullName) {
        this.code = code;
        this.sign = sign;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Currensy{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", sign='" + sign + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
