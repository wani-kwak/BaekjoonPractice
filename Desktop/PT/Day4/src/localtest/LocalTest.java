package localtest;

public class LocalTest {
	
	int r;
	
	public void test1() {
		
		int a = 10; 
		int b = 20;
		r = a+b; // r이 만들어진 것은 공통(전역변수이니까) 참조가
		
//		return r;
	}
	
	public void test2() {
		
//		int result = test1();
		System.out.println(r);
		
	}

}
