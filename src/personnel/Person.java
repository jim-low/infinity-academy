package personnel;

import payment.Card;

public class Person {
    private String name;
    private String password;
    private String gender;
    private String email;
    private Card card;

    public Person(){
        this("","","", "", null);
    }

    public Person(Person person){
        this(person.name, person.password, person.gender, person.email, person.card);
    }

    public Person(String name) {
        this(name, "", "","", null);
    }

    public Person(String name, String password, String email) {
        this(name, password ,"", email, null);
    }

    public Person(String name,String password, String gender, String email, Card card) {
        this.name = name;
        this.gender = gender;
        this.password = password;
        this.email = email;
        this.card = card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public static <T> T search (String email, String password, Class<T> type){
        T person = null;

        if(type.equals(Student.class)){
            person = type.cast(Student.search(email, password));
        } else if(type.equals(Instructor.class)){
            person = type.cast(Instructor.search(email, password));
        }

        return person;
    }
 }
