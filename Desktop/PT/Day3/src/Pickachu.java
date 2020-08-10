
public class Pickachu {
	
	private int age;
	public int speed;
	
	public Pickachu() { //생성자 만들기
		System.out.println("Basic Constructor");
	}
	
	public Pickachu(int age, int speed) {
		this.age = age;
		this.speed = speed;
		System.out.println("Parameter Constructor");
		
	}
	
	public void attack() {
		System.out.println("100만 볼트!!!");
	}
	
	public void info() {
		System.out.println(age);
		System.out.println(speed);
	}	

}

