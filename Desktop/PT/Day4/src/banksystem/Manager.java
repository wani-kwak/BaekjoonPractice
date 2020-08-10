package banksystem;

import java.util.Scanner;

public class Manager {
	
	Bank myBank;
	
	User user1;
	User user2;
	User user3;
	
	public Manager() {
		initSystem();
	}
	
	private void sendMoney(User u1, User u2, int balance) {
		myBank.sendMoney(u1, u2, balance);
	}
	
	private void withDrawal(User u1, int balance) {
		myBank.withDrawal(u1, balance);
	}
	
	private void deposit(User u1, int balance) {
		myBank.deposit(u1, balance);
	}
	
	
	private void initSystem() {
		System.out.println("시스템 가동");
		user1 = new	User("곽병완",350000);
		user2 = new User("조지니",400000);
		user3 = new User("똥똥이",300000);
		myBank = new Bank("국민은행");
		
		displayAll();
		
		System.out.println("[1]송금");
		System.out.println("[2]출금");
		System.out.println("[3]입금");
		System.out.println("거래 할 번호를 입력해주세요");
		
		Scanner scan = new Scanner(System.in);
		int select = scan.nextInt();
		scan.nextLine(); // 버퍼 비우기
		
		if(select == 1) {
			System.out.println("송금을 선택하였습니다.");
			System.out.println("송금 보낼 사용자의 이름을 입력하세요.");
			String name = scan.nextLine();
			
			if(name.equals(user1.name)) {
				System.out.println("송금 받을 사용자를 입력해주세요. ");
				String name2 = scan.nextLine();
				
				if(name2.equals(user2.name)) {
					System.out.println("얼마를 송금할까요?");
					int money = scan.nextInt();
					System.out.println(user2.name+"에게 "+money+"원을 송금합니다.");
					sendMoney(user1,user2,money);
					
				}
				else if(name2.equals(user3.name)) {
					System.out.println("얼마를 송금할까요?");
					int money = scan.nextInt();
					System.out.println(user3.name+"에게 "+money+"원을 송금합니다.");
					sendMoney(user1,user3,money);
				}
				displayAll();
			}
			
			if(name.equals(user2.name)) {
				System.out.println("송금 받을 사용자를 입력해주세요.");
				String name2 = scan.nextLine();
				
				if(name2.equals(user1.name)) {
				  System.out.println("얼마를 송금할까요?");
				  int money = scan.nextInt();
				  System.out.println(user1.name+"에게 "+money+"원을 송금합니다.");
				  sendMoney(user2,user1,money);
				}
				else if(name2.equals(user3.name)) {
				  System.out.println("얼마를 송금할까요?");
				  int money = scan.nextInt();
				  System.out.println(user3.name+"에게 "+money+"원을 송금합니다.");
				  sendMoney(user2,user3,money);	
				}
				displayAll();
			}
			
			if(name.equals(user3.name)) {
				System.out.println("송금 받을 사용자를 입력해주세요.");
				String name2 = scan.nextLine();
				
				if(name2.equals(user1.name)) {
				  System.out.println("얼마를 송금할까요?");
				  int money = scan.nextInt();
				  System.out.println(user1.name+"에게 "+money+"원을 송금합니다.");
				  sendMoney(user3,user1,money);
				}
				else if(name2.equals(user2.name)) {
				  System.out.println("얼마를 송금할까요?");
				  int money = scan.nextInt();
				  System.out.println(user2.name+"에게 "+money+"원을 송금합니다.");
				  sendMoney(user3,user2,money);	
				}
				displayAll();
			}
			
		}
		else if(select == 2) {
			System.out.println("출금을 선택하였습니다.");
			System.out.println("어느 사용자 계좌에서 출금하시겠습니까?");
			String name = scan.nextLine(); // 안되네 실행이 ㅠ 버퍼를 비워줘야된다,(해결책)
			System.out.println("얼마를 출금할까요?");
			int money = scan.nextInt();
			
			if(name.equals(user1.name)){
				System.out.println(user1.name+"에서 "+money+"원을 출금합니다.");
				withDrawal(user1,money);
			}
			
			else if(name.equals(user2.name)) {
				System.out.println(user2.name+"에서 "+money+"원을 출금합니다.");
				withDrawal(user2,money);
			}
			
			else if(name.equals(user3.name)) {
				System.out.println(user3.name+"에서 "+money+"원을 출금합니다.");
				withDrawal(user3,money);
			}
			
			displayAll();
				
		}
		else if(select == 3) {
			System.out.println("입금을 선택하였습니다.");
			System.out.println("누구에게 입금을 하시겠습니까?");
			String name = scan.nextLine();
			System.out.println("얼마를 입금하시겠습니까?");
			int money = scan.nextInt();
			
			if(name.equals(user1.name)){
				System.out.println(user1.name+"에게 입금을 합니다.");
				deposit(user1,money);
			}
			
			else if(name.equals(user2.name)) {
				System.out.println(user2.name+"에게 입금을 합니다.");
				deposit(user2,money);
			}
			
			else if(name.equals(user3.name)) {
				System.out.println(user3.name+"에게 입금을 합니다.");
				deposit(user3,money);
			}
			
			displayAll();
					
		}
		
		else {
			System.out.println("올바른 번호를 입력해주세요!!");
		}
	}
	
	private void displayAll() {
		
		System.out.println("User1 이름: " + user1.name);
		System.out.println("User1 잔액: " + user1.balance);
		System.out.println("User2 이름: " + user2.name);
		System.out.println("User2 잔액: " + user2.balance);
		System.out.println("User3 이름: " + user3.name);
		System.out.println("User3 잔액: " + user3.balance);
				
		
	}

}
