import java.util.*;
import java.io.*;

public class Main {
	static int N, M, K;
	static Turret[][] map;
	static int result = 0;
	static int turn = 1;
	static Node attacker;
	static Node target;
	static List<Node> attackOrder = new ArrayList<>();
	static boolean[][] visited;
	static boolean[][] refurbMap;
	static Stack<Node> laserStack;
	static boolean laserAttack;
	static int[] dx = { 0, 1, 0, -1 };
	static int[] dy = { 1, 0, -1, 0 };

	public static class Turret {
		int hp;
		boolean out;

		public Turret(int hp, boolean out) {
			this.hp = hp;
			this.out = out;
		}
	}

	public static class Node {
		int x;
		int y;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static boolean checkRemaining() {
		int cnt = 0;
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++)
				if (!map[i][j].out)
					cnt++;
		}
		if (cnt == 1) {
			return false;
		}
		return true;
	}

	public static void getAttackerAndTarget() {
		attacker = new Node(0, 0);
		target = new Node(0, 0);
		List<Node> weakest = new ArrayList<>();
		List<Node> strongest = new ArrayList<>();

		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
				if (!map[i][j].out) {
					if (weakest.isEmpty())
						weakest.add(new Node(i, j));
					else if (map[weakest.get(0).x][weakest.get(0).y].hp == map[i][j].hp)
						weakest.add(new Node(i, j));
					else if (map[weakest.get(0).x][weakest.get(0).y].hp > map[i][j].hp) {
						weakest.clear();
						weakest.add(new Node(i, j));
					}
					if (strongest.isEmpty())
						strongest.add(new Node(i, j));
					else if (map[strongest.get(0).x][strongest.get(0).y].hp == map[i][j].hp)
						strongest.add(new Node(i, j));
					else if (map[strongest.get(0).x][strongest.get(0).y].hp < map[i][j].hp) {
						strongest.clear();
						strongest.add(new Node(i, j));
					}
				}
			}
		}
		if (weakest.size() == 1) {
			attacker = weakest.get(0);
		} else {
			ListIterator<Node> li = attackOrder.listIterator(attackOrder.size());
			boolean tok = false;
			while (li.hasPrevious()) {
				Node curNode = li.previous();
				for (Node n : weakest) {
					if (n.x == curNode.x && n.y == curNode.y) {
						attacker = curNode;
						tok = true;
						break;
					}
				}
				if (tok)
					break;
			}
			if (attacker.x == 0 && attacker.y == 0) {
				List<Node> tempList = new ArrayList<>();
				li = weakest.listIterator(weakest.size());
				while (li.hasPrevious()) {
					Node curNode = li.previous();
					if (tempList.isEmpty() || curNode.x + curNode.y == tempList.get(0).x + tempList.get(0).y) {
						tempList.add(curNode);
					} else if (curNode.x + curNode.y > tempList.get(0).x + tempList.get(0).y) {
						tempList.clear();
						tempList.add(curNode);
					}
				}
				if (tempList.size() == 1)
					attacker = tempList.get(0);
				else {
					li = weakest.listIterator(weakest.size());
					while (li.hasPrevious()) {
						Node curNode = li.previous();
						if ((attacker.x == 0 && attacker.y == 0) || attacker.y < curNode.y)
							attacker = curNode;
					}
				}
			}
		}
		if (strongest.size() == 1) {
			target = strongest.get(0);
		} else {
			ListIterator<Node> li = attackOrder.listIterator(attackOrder.size());
			Stack<Node> tempStack = new Stack<>();
			while (li.hasPrevious()) {
				Node curNode = li.previous();
				for(int i=0; i<strongest.size(); i++) {
					Node n = strongest.get(i);
					if (n.x == curNode.x && n.y == curNode.y) {
						tempStack.push(n);
						strongest.remove(n);
						i--;
					}
				}
			}
			if (strongest.size() == 1)
				target = strongest.get(0);
			else if (strongest.size() == 0)
				target = tempStack.pop();
			if (target.x == 0 && target.y == 0) {
				List<Node> tempList = new ArrayList<>();
				li = strongest.listIterator();
				while (li.hasNext()) {
					Node curNode = li.next();
					if (tempList.isEmpty() || curNode.x + curNode.y == tempList.get(0).x + tempList.get(0).y) {
						tempList.add(curNode);
					} else if (curNode.x + curNode.y < tempList.get(0).x + tempList.get(0).y) {
						tempList.clear();
						tempList.add(curNode);
					}
				}
				if (tempList.size() == 1)
					target = tempList.get(0);
				else {
					li = strongest.listIterator();
					while (li.hasNext()) {
						Node curNode = li.next();
						if ((target.x == 0 && target.y == 0) || target.y < curNode.y)
							target = curNode;
					}
				}
			}
		}
		for (Node n : attackOrder) {
			if (n.x == attacker.x && n.y == attacker.y) {
				attackOrder.remove(n);
				break;
			}

		}
		attackOrder.add(attacker);
	}

	public static void dfs(Node s, Node e) {
		visited[s.x][s.y] = true;
		if (s.x == e.x && s.y == e.y) {
			laserAttack = true;
			return;
		}
		for (int d = 0; d < 4; d++) {
			int nx = s.x + dx[d];
			int ny = s.y + dy[d];
			if (nx <= 0)
				nx += N;
			if (ny <= 0)
				ny += M;
			if (nx > N)
				nx -= N;
			if (ny > M)
				ny -= M;

			if (nx == e.x && ny == e.y) {
				laserStack.push(new Node(nx, ny));
				visited[nx][ny] = true;
				laserAttack = true;
				return;
			}
		}
		for (int d = 0; d < 4; d++) {
			int nx = s.x + dx[d];
			int ny = s.y + dy[d];
			if (nx <= 0)
				nx += N;
			if (ny <= 0)
				ny += M;
			if (nx > N)
				nx -= N;
			if (ny > M)
				ny -= M;

			if (!visited[nx][ny] && !map[nx][ny].out) {
				laserStack.push(new Node(nx, ny));
				dfs(new Node(nx, ny), e);
				if (laserAttack)
					return;
				visited[nx][ny] = false;
				laserStack.pop();
			}
		}
	}

	public static void damage(Node node, int damage) {
		map[node.x][node.y].hp -= damage;
		refurbMap[node.x][node.y] = true;
		if (map[node.x][node.y].hp <= 0) {
			map[node.x][node.y].hp = 0;
			map[node.x][node.y].out = true;
		}
	}

	public static void bombAttack() {
		int[] rdx = { 1, 1, 1, 0, -1, -1, -1, 0 };
		int[] rdy = { 1, 0, -1, -1, -1, 0, 1, 1 };
		damage(target, map[attacker.x][attacker.y].hp);
		for (int d = 0; d < 8; d++) {
			int nx = target.x + rdx[d];
			int ny = target.y + rdy[d];
			if (nx <= 0)
				nx += N;
			if (ny <= 0)
				ny += M;
			if (nx > N)
				nx -= N;
			if (ny > M)
				ny -= M;
			if (!map[nx][ny].out)
				damage(new Node(nx, ny), map[attacker.x][attacker.y].hp / 2);
		}

	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new Turret[N + 1][M + 1];

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= M; j++) {
				int thp = Integer.parseInt(st.nextToken());
				map[i][j] = new Turret(thp, (thp == 0) ? true : false);
			}
		}
		while (turn <= K) {
			getAttackerAndTarget(); // 공격자,타깃 선정//
//			// 바보
//			System.out.println("turn " + turn);
//			System.out.println("attacker: " + attacker.x + " " + attacker.y);
//			System.out.println("target: " + target.x + " " + target.y);
			map[attacker.x][attacker.y].hp += (N + M);
			laserAttack = false; // 레이저 공격
			visited = new boolean[N + 1][M + 1];
			refurbMap = new boolean[N + 1][M + 1];
			laserStack = new Stack<>();
			visited[attacker.x][attacker.y] = true;
			dfs(attacker, target);
			if (laserAttack) {
				laserStack.pop();
				damage(target, map[attacker.x][attacker.y].hp);
				while (!laserStack.isEmpty()) {
					damage(laserStack.pop(), map[attacker.x][attacker.y].hp / 2);

				}
			} else if (!laserAttack)
				bombAttack();
			refurbMap[attacker.x][attacker.y] = true;
			for (int i = 1; i <= N; i++) {
				for (int j = 1; j <= M; j++) {
					if (!map[i][j].out && !refurbMap[i][j]) {
						refurbMap[i][j] = true;
						map[i][j].hp++;
					}
				}
			}

//			// 바보
//			System.out.println((laserAttack) ? "laserattack" : "bombattack");
//			for (int i = 1; i <= N; i++) {
//				for (int j = 1; j <= M; j++) {
//					System.out.print(map[i][j].hp + " ");
//				}
//				System.out.print("\n");
//			}
//			System.out.print("\n");
//			//

			if (!checkRemaining())
				break;
			turn++;
		}
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
				if (!map[i][j].out)
					result = Math.max(result, map[i][j].hp);
			}
		}

		System.out.println(result);
	}
}