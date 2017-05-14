package ms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class MinesweeperCmd {

	private static final int WIDTH = 15;
	private static final int HEIGHT = 8;
	private static final int TOTAL_MINES = 7;
	private static final String[] DISP_MINE_CNT = {"□", "１", "２", "３", "４", "５", "６", "７", "８"};
	private static final String[] COORDINATE_NUM = {"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳"};
	private static boolean[][] mineMap = null;
	private static String[][] dispMap = null;
	private static LinkedList<String> searchReserve = new LinkedList<>();

	public static void main(String args[]) {
		int x;
		int y;
		String readX;
		String readY;
		int notOpenPanelCnt = 0;
		boolean firstOpen = true;

		// 初期化
		init();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				notOpenPanelCnt = 0;

				while (true) {
					System.out.print("X座標を入力してください：");
					readX = br.readLine();

			    	if (inputCheck(WIDTH, readX)) {
			    		x = Integer.parseInt(readX) - 1;
						break;
			    	}
				}

				while (true) {
					System.out.print("Y座標を入力してください：");
					readY = br.readLine();

			    	if (inputCheck(HEIGHT, readY)) {
			    		y = Integer.parseInt(readY) - 1;
						break;
			    	}
				}
				System.out.println("");


		    	//初手で地雷を踏むことがないようにする
		    	if (firstOpen) {
		    		firstOpen = false;

	    			if (mineMap[y][x]) {
	    				addMine();
	    				mineMap[y][x] = false;
	    			}
		    	} else {
		    		if (mineMap[y][x]) {
		    			//地雷を踏んだ場合
		    			dispAnswer();
		    			System.out.println("ゲームオーバー！");
		    			break;
		    		}
		    	}

		    	//探索予約
		    	searchReserve.offer(x + "," + y);
		    	search();

				//表示
				disp();
				//dispAnswer();

				//クリア判定
				for (int i = 0; i < HEIGHT; i++) {
					for (int j = 0; j < WIDTH; j++) {
						if (dispMap[i][j].equals("■")) {
							notOpenPanelCnt++;
						}
					}
				}

				if (notOpenPanelCnt == TOTAL_MINES) {
					System.out.println("★☆★ゲームクリア★☆★");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();;
		}
	}

	private static boolean inputCheck(int max, String readStr) {
		int target;

		try {
	    	target = Integer.parseInt(readStr) - 1;
		} catch (NumberFormatException e) {
			return false;
		}

		if (target < 0 || max <= target) {
			return false;
		}

		return true;
	}

	private static void search() {
		String tmp[] = null;
		int x;
		int y;
		int numberOfMines;

		while (searchReserve.size() != 0) {
			numberOfMines = 0;
			tmp = searchReserve.poll().split(",");
			x = Integer.parseInt(tmp[0]);
			y = Integer.parseInt(tmp[1]);

			numberOfMines += searchTarget(x - 1, y - 1);
			numberOfMines += searchTarget(x, y - 1);
			numberOfMines += searchTarget(x + 1, y - 1);
			numberOfMines += searchTarget(x - 1, y);
			numberOfMines += searchTarget(x + 1, y);
			numberOfMines += searchTarget(x - 1, y + 1);
			numberOfMines += searchTarget(x, y + 1);
			numberOfMines += searchTarget(x + 1, y + 1);

			//System.out.println(x + "," + y + "," + numberOfMines);

			//地雷個数を取得する
			dispMap[y][x] = DISP_MINE_CNT[numberOfMines];

			if (numberOfMines == 0) {
				//８方向を調査対象とする
				addSearchReserve(x - 1, y - 1);
				addSearchReserve(x, y - 1);
				addSearchReserve(x + 1, y - 1);
				addSearchReserve(x - 1, y);
				addSearchReserve(x + 1, y);
				addSearchReserve(x - 1, y + 1);
				addSearchReserve(x, y + 1);
				addSearchReserve(x + 1, y + 1);
			}
		}
	}

	private static void addSearchReserve(int x, int y) {
		//System.out.println(">>addSearchReserve");

		if (x < 0 || WIDTH <= x) {
			return;
		}

		if (y < 0 || HEIGHT <= y) {
			return;
		}

		if (!dispMap[y][x].equals("■")) {
			return;
		}

		String reserveVal = x + "," + y;

		for(String tmp: searchReserve) {
			if (tmp.equals(reserveVal)) {
				return;
			}
		}

		searchReserve.offer(reserveVal);
	}

	private static int searchTarget(int x, int y) {
		//System.out.println(">>searchTarget");

		if (x < 0 || WIDTH <= x) {
			return 0;
		}

		if (y < 0 || HEIGHT <= y) {
			return 0;
		}

		//System.out.println(x + "," + y);

		if (mineMap[y][x]) {
			return 1;
		}

		return 0;
	}

	private static void init() {
		mineMap = new boolean[HEIGHT][WIDTH];
		dispMap = new String[HEIGHT][WIDTH];

		//地雷、画面表示用配列を初期化
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				mineMap[i][j] = false;
				dispMap[i][j] = "■";
			}
		}

		//地雷を配置
		for (int i = 0; i < TOTAL_MINES; i++) {
			addMine();
		}

//		mineMap[0][8] = true;
//		mineMap[1][7] = true;
//		mineMap[1][10] = true;
//		mineMap[1][13] = true;
//		mineMap[2][9] = true;
//		mineMap[2][12] = true;
//		mineMap[4][7] = true;
//		mineMap[4][12] = true;
//		mineMap[5][12] = true;
//		mineMap[7][8] = true;

		//表示
		disp();
	}

	private static void addMine() {
		int mineX = (int)(Math.random() * WIDTH);
		int mineY = (int)(Math.random() * HEIGHT);

		if (mineMap[mineY][mineX]) {
			addMine();
		}

		//地雷を配置
		mineMap[mineY][mineX] = true;
	}

	private static void disp() {
		printCoodinateHeader();

		for (int i = 0; i < HEIGHT; i++) {
			printCoodinateRow(i);

			for (int j = 0; j < WIDTH; j++) {
				System.out.print(dispMap[i][j]);
			}

			System.out.println();
		}
		System.out.println();
	}

	private static void printCoodinateHeader() {
		System.out.print("　┃");
		for (int i = 0; i < WIDTH; i++) {
			System.out.print(COORDINATE_NUM[i]);
		}
		System.out.println("");
		System.out.println("━╋━━━━━━━━━━━━━━━");
	}

	private static void printCoodinateRow(int i) {
		System.out.print(COORDINATE_NUM[i]);
		System.out.print("┃");
	}

	private static void dispAnswer() {
		printCoodinateHeader();

		for (int i = 0; i < HEIGHT; i++) {
			printCoodinateRow(i);

			for (int j = 0; j < WIDTH; j++) {
				if (mineMap[i][j]) {
					System.out.print("◎");
				} else {
					System.out.print(dispMap[i][j]);
				}
			}

			System.out.println();
		}
	}
}
