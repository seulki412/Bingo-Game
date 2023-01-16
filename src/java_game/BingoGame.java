package java_game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;


public class BingoGame {
	static JPanel panelNorth; //프레임 내 상단
	static JPanel panelSouth; //프레임 내 하단
	static JPanel panelCenter; // 프레임 내 게임화면
	static JLabel labelMessageNorth; //상단 문자 
	static JLabel labelMessageSouth; //하단 문자
	static JButton[] buttons = new JButton[16];
	static String[] fruitImages = {
			"apple.png", "bananas.png", "grapes.png", "lemon.png",
			"orange-juice.png", "passion-fruit.png", "strawberry.png", "watermelon.png",
			"apple.png", "bananas.png", "grapes.png", "lemon.png",
			"orange-juice.png", "passion-fruit.png", "strawberry.png", "watermelon.png"
	};
	
	static String[] cardImages = {
			"ace-of-clubs.png","ace-of-hearts.png","eight-of-diamonds.png", "eight-of-hearts.png",
			"nine-of-clubs.png", "nine-of-hearts.png", "seven-of-clubs.png", "seven-of-hearts.png",
			"ace-of-clubs.png","ace-of-hearts.png","eight-of-diamonds.png", "eight-of-hearts.png",
			"nine-of-clubs.png", "nine-of-hearts.png", "seven-of-clubs.png", "seven-of-hearts.png"
	};
	
	static int openCount = 0; //열린 카드 카운트
	static int buttonIndexSave1 = 0; // 첫번째 열린 카드 인덱스 0~15
	static int buttonIndexSave2 = 0; // 첫번째 열린 카드 인덱스 0~15
	static javax.swing.Timer timer;
	static int tryCount = 0; //시도한 횟수
	static int successCount = 0; //빙고 횟수 0~8
	static String playerName;
	static int gameOver;
	static String[] gameLevel = {"Normal", "Hard"}; 
	static int levelNum = 0;
	
	static int[] successIndex = new int[16]; //성공한 인덱스 담는 배열 
	static int index;
	
	static class MyFrame extends JFrame implements ActionListener{  //틀 만들기  //ActionListener 인터페이스 추가
		public MyFrame(String title) { //생성자
			super(title);
			this.setLayout(new BorderLayout()); //상(North),하,좌,우,센터(Center)
			this.setSize(400, 500); //전체 크기 400,500픽셀
			this.setVisible(true); //보이기
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //닫기
			
			initUI(this); //Screen UI set.
			//initUI메소드에 MyFrame의 자신 this를 파라미터로 넘김
			this.pack(); // 여백부분 정리
			player_name();
			game_level();
			mixCard(); //카드 배열을 섞음.
			for(int i = 0; i < successIndex.length; i++) {
				successIndex[i] = 20;  // 버튼 인덱스와 곂치지 않는 숫자로 초기화
			}
		}
		@Override
			public void actionPerformed(ActionEvent e) { //ActionListener 인터페이스 구현 //버튼이 눌리는 event생기면 여기 콜백
			// 인터페이스 : 부모는 선언만 하므로 자식에서 오버라이딩해서 재정의 해야함	
			//System.out.println("Button Clicked"); // 확인용
				
			if(openCount == 2) { // 한번 클릭했을 때 아직 openCount가 증가하지 않았으므로 판정로직 전까지는 1click = openCount0 / 2click = openCount1 / 3번째 누르면 2가됨.
				return;  //if문의 return은 if문을 실행시킨 actionPerformed 메서드까지 종료
			}
			
				
				JButton btn = (JButton)e.getSource(); //눌리는 이벤트가 발생한 버튼 객체 가져오기
					index = getButtonIndex(btn); //버튼 객체를 생성하고 받아서, 눌린 버튼의 인덱스 받아오기
//				System.out.println("Button Index : " + index); // 확인용
				
				if(successIndex[index] != 20) {//클릭한 카드가 이미 뒤집힌 카드라면
					System.out.println("카드를 다시 선택하세요");
					reBackToQuestion();
					openCount = 0;
					return;
				}
				
				if(levelNum == 0) {
					btn.setIcon(changeImage(fruitImages[index]));  //실제 눌린 버튼의 눌렸을 때 이미지 변경
				}else if(levelNum == 1) {
					btn.setIcon(changeImage(cardImages[index]));  //실제 눌린 버튼의 눌렸을 때 이미지 변경
				}
				
				
				openCount++; // 버튼을 누르면 1씩 증가
				
				//비교를 위한 누른 이미지 저장
				if(openCount == 1) {
					buttonIndexSave1 = index;
				}else if(openCount == 2) {
					buttonIndexSave2 = index;
					tryCount++;  //그림을 2개 눌렀을 때 시도횟수 1씩 증가
					labelMessageNorth.setText("Find Same Card !");
					labelMessageSouth.setText( "Player : "+ playerName+",   Try " + tryCount + "회");
					
					//두번 째 카드 눌렀을 때 판정로직
					boolean isBingo = checkCard(buttonIndexSave1, buttonIndexSave2);
					if(isBingo == true) { //같은 그림 일때
						openCount = 0;
						successCount++;
						
						successIndex[buttonIndexSave1] = buttonIndexSave1;
						successIndex[buttonIndexSave2] = buttonIndexSave2;
//						System.out.println(Arrays.toString(successIndex)); //확인용
						
						
						
						if(successCount == 8) { //모두 찾았을 때
							labelMessageNorth.setText("Game Over !");
							//다시시작하겠습니까?
							reStart();
							
						}
					}else if(isBingo == false){
						
								backToQuestion();
							
						
					}
				}
		}
			
			public void game_level() {
				levelNum = JOptionPane.showOptionDialog(null, "게임 난이도를 선택하세요", "Bingo Game", JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, null, gameLevel,"Normal");
				if(levelNum == 0) { //난이도 쉬움 -과일 이미지
					levelNum = 0;
				}else if(levelNum == 1) {//난이도 어려움 -> 카드 이미지
					levelNum = 1;
				}
			}
		
		
			public void player_name() {
				playerName = JOptionPane.showInputDialog("이름을 입력하세요.");
			}
			
			public void reStart() {
				gameOver = JOptionPane.showConfirmDialog(panelCenter, "플레이어 : " + playerName +", 도전 : "  +tryCount + "회"+ "\n게임을 다시 시작하시겠습니까?");
				if(gameOver == 0) { // '네'를 누르면 int 0 반환 
					tryCount = 0;
					successCount = 0;
					this.setVisible(false); // 창닫기
					new MyFrame("Bingo Game"); //게임 다시시작 생성자 호출
				}else if(gameOver == 1) { //'아니오'를 누르면 int 1반환
					this.setVisible(false); // 창닫기
				}
			}
		
			public void backToQuestion() { //그림이 다를 경우 다시 원래 그림으로 뒤집기
				timer = new javax.swing.Timer(1000, new ActionListener() {  //1초후 액션 실행
					@Override
					public void actionPerformed(ActionEvent e) {
//						System.out.println("Timer"); //확인 용
						
						openCount = 0;
						buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
						buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
						timer.stop();
					}
				});
				timer.start();
			}
			
			public void reBackToQuestion() { // 이미 뒤집힌 카드 제외하고 다시 퀘스천 이미지로 뒤집기
				timer = new javax.swing.Timer(1000, new ActionListener() {  //1초후 액션 실행
					@Override
					public void actionPerformed(ActionEvent e) {
//						System.out.println("Timer"); //확인 용
						
						openCount = 0;
						if(successIndex[buttonIndexSave1] == 20 ) { //아직 성공 못한 카드만 퀘스천으로 뒤집기
							buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
							timer.stop();
						}else if(successIndex[buttonIndexSave2]== 20) {
							buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
							timer.stop();
						}
					}
				});
				timer.start();
			}
		
			public boolean checkCard(int index1, int index2 ) {
				if(index1 == index2) {  //같은 카드를 2번 눌렀을 경우
					return false;
				}else if(levelNum == 0 && (fruitImages[index1].equals(fruitImages[index2]))) {
					return true;
				}else if(levelNum == 1 && (cardImages[index1].equals(cardImages[index2]))) {
					return true;
				}return false;
				
			}
			
			
			
			public int getButtonIndex(JButton btn) {
				int index = 0;  //for문을 돌면서 어떤 버튼이 눌렸는지 확인하여 인덱스 반환
				for(int i = 0; i < 16; i++) {
					if(buttons[i] == btn) { //눌린 버튼 객체와 배열의 객체가 같은지
						index = i;
					}
				}
				return index;
			}
	}
	static void mixCard() {
		Random random = new Random();
		for(int i = 0; i < 1000; i++) {  //카드를 1000번 섞음
			int randomNum = random.nextInt(15) + 1; //1~15까지의 난수
			//섞는 로직
			if(levelNum == 0) {
				String temp = fruitImages[0];
				fruitImages[0] = fruitImages[randomNum]; //1~15까지의 난수를 뽑아 인덱스로 반환
				fruitImages[randomNum] = temp;  // 난수의 인덱스 이미지 자리에 다시 0번째 이미지 반환하면서 각 이미지 2개씩 유지
			}else if(levelNum == 1) {
				String temp = cardImages[0];
				cardImages[0] = cardImages[randomNum]; //1~15까지의 난수를 뽑아 인덱스로 반환
				cardImages[randomNum] = temp;  // 난수의 인덱스 이미지 자리에 다시 0번째 이미지 반환하면서 각 이미지 2개씩 유지
			}
		}
		
	}
	
	static void initUI(MyFrame myFrame) {
		panelNorth = new JPanel();
		panelNorth.setPreferredSize(new Dimension(400,100));
		panelNorth.setBackground(Color.orange);
		labelMessageNorth = new JLabel("Find Same Card !");
		labelMessageNorth.setPreferredSize(new Dimension(400, 100));
		labelMessageNorth.setForeground(Color.WHITE);
		labelMessageNorth.setFont(new Font("궁서 보통",Font.BOLD, 30));
		labelMessageNorth.setHorizontalAlignment(JLabel.CENTER); //센터로 글자 정렬 -> 가운데 정렬
		panelNorth.add(labelMessageNorth); //글자메세지 UI를 설정하여 위쪽 panel에 생성
		myFrame.add("North",panelNorth); //글자와 panel을 위쪽에 UI로 생성
		
		panelSouth = new JPanel();
		panelSouth.setPreferredSize(new Dimension(400, 50));
		panelSouth.setBackground(Color.orange);
		labelMessageSouth = new JLabel("Player : " + "    Try 0");
		labelMessageSouth.setPreferredSize(new Dimension(400,50));
		labelMessageSouth.setForeground(Color.white);
		labelMessageSouth.setFont(new Font("궁서 보통", Font.BOLD, 25));
		labelMessageSouth.setHorizontalAlignment(JLabel.CENTER);
		panelSouth.add(labelMessageSouth);
		myFrame.add("South",panelSouth);
		
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(4,4)); //4 * 4
		panelCenter.setPreferredSize(new Dimension(400,400));
		for(int i = 0; i < 16; i++) {
			buttons[i] = new JButton(); //버튼 객체 생성
			buttons[i].setPreferredSize(new Dimension(80,80));
			buttons[i].setBackground(Color.white);
			buttons[i].setIcon(changeImage("question.png"));
			buttons[i].addActionListener(myFrame);  //버튼이 눌릴 때 이벤트
			panelCenter.add(buttons[i]);  //게임화면 부분에 버튼 추가
		}
		myFrame.add("Center", panelCenter);
		
	} 
	
	static ImageIcon changeImage(String filename) {
		ImageIcon icon = new ImageIcon();
		if(levelNum == 0) {
			 icon = new ImageIcon("./img/" + filename); //아이콘을 img 폴더에 있는 이미지를 읽어와 객체로 생성
		}else if(levelNum == 1) {
			 icon = new ImageIcon("./card/" + filename); //아이콘을 img 폴더에 있는 이미지를 읽어와 객체로 생성
		}
		Image originImage = icon.getImage(); //imageIcon 객체에서 image를 추출
		Image changedImage = originImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH); //사진의 크기 조정하여 새로운 이미지 객체 생성
		ImageIcon icon_new = new ImageIcon(changedImage); //조정한 사진을 담은 아이콘 객체 생성
		return icon_new;
		
	}
	public static void main(String[] args) {
		new MyFrame("Bingo Game"); //생성자 호출
		
	}

}
