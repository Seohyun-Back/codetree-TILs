import java.util.*;
import java.io.*;

public class Main {
	static int L, N, Q;
	static int[][] map;
	static int[][] map2;
	static int[] dx = { -1, 0, 1, 0 };
	static int[] dy = { 0, 1, 0, -1 };
	static Knight[] knights;
	static Queue<Node> orders;
	static Stack<Integer> stack;
	static Stack<Integer> stack2;
	static int answer = 0;

	public static class Node {
		int x;
		int y;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class Knight {
		int n;
		Node pos;
		int h;
		int w;
		int k;
		boolean out;
		int damaged;

		public Knight(int n, Node pos, int h, int w, int k, boolean out, int damaged) {
			this.n = n;
			this.pos = pos;
			this.h = h;
			this.w = w;
			this.k = k;
			this.out = false;
			this.damaged = damaged;
		}
	}

	public static int findTrap(int kn) {
		Knight k = knights[kn];
		int result = 0;
		for (int i = k.pos.x; i < k.pos.x + k.h; i++) {
			for (int j = k.pos.y; j < k.pos.y + k.w; j++) {
				if (map[i][j] == -1)
					result += map[i][j];
			}
		}
		return result;
	}

	public static void move(int kn, int dir) {
		Knight k = knights[kn];
		switch (dir) {
		case 0:
			for (int j = k.pos.y; j < k.pos.y + k.w; j++) {
				map2[k.pos.x - 1][j] = kn;
				map2[k.pos.x + k.h - 1][j] = 0;
			}
			knights[kn].pos.x += dx[dir];
			break;
		case 1:
			for (int i = k.pos.x; i < k.pos.x + k.h; i++) {
				map2[i][k.pos.y + k.w] = k.n;
				map2[i][k.pos.y] = 0;
			}
			knights[kn].pos.y += dy[dir];
			break;
		case 2:
			for (int j = k.pos.y; j < k.pos.y + k.w; j++) {
				map2[k.pos.x + k.h][j] = k.n;
				map2[k.pos.x][j] = 0;
			}
			knights[kn].pos.x += dx[dir];
			break;
		case 3:
			for (int i = k.pos.x; i < k.pos.x + k.h; i++) {
				map2[i][k.pos.y - 1] = k.n;
				map2[i][k.pos.y + k.w - 1] = 0;
			}
			knights[kn].pos.y += dy[dir];
			break;
		}
	}

	public static boolean checkMovable(int kn, int dir) {
		if (stack.contains(kn))
			return true;
		if (!stack.contains(kn))
			stack.push(kn);
		Knight k = knights[kn];
		switch (dir) {
		case 0:
			if (k.pos.x - 1 <= 0)
				return false;
			for (int j = k.pos.y; j < k.pos.y + k.w; j++) {
				if (map[k.pos.x - 1][j] == -2)
					return false;
				if (map2[k.pos.x - 1][j] >= 1) {
					if (!checkMovable(map2[k.pos.x - 1][j], dir))
						return false;
				}
			}
			break;
		case 1:
			if (k.pos.y + k.w > L)
				return false;
			for (int i = k.pos.x; i < k.pos.x + k.h; i++) {
				if (map[i][k.pos.y + k.w] == -2)
					return false;
				if (map2[i][k.pos.y + k.w] >= 1) {
					if (!checkMovable(map2[i][k.pos.y + k.w], dir))
						return false;
				}
			}
			break;
		case 2:
			if (k.pos.x + k.h > L)
				return false;
			for (int j = k.pos.y; j < k.pos.y + k.w; j++) {
				if (map[k.pos.x + k.h][j] == -2)
					return false;
				if (map2[k.pos.x + k.h][j] >= 1) {
					if (!checkMovable(map2[k.pos.x + k.h][j], dir))
						return false;
				}
			}
			break;
		case 3:
			if (k.pos.y - 1 <= 0)
				return false;
			for (int i = k.pos.x; i < k.pos.x + k.h; i++) {
				if (map[i][k.pos.y - 1] == -2)
					return false;
				if (map2[i][k.pos.y - 1] >= 1) {
					if (!checkMovable(map2[i][k.pos.y - 1], dir))
						return false;
				}
			}
			break;
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		map = new int[L + 1][L + 1];
		map2 = new int[L + 1][L + 1]; // 기사 위치만
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				int t = Integer.parseInt(st.nextToken());
				if (t == 0)
					map[i][j] = t;
				else if (t == 1)
					map[i][j] = -1; // 함정
				else if (t == 2)
					map[i][j] = -2; // 벽
			}
		}
		knights = new Knight[N + 1];
		for (int t = 1; t <= N; t++) {
			st = new StringTokenizer(br.readLine());
			int tr = Integer.parseInt(st.nextToken());
			int tc = Integer.parseInt(st.nextToken());
			int th = Integer.parseInt(st.nextToken());
			int tw = Integer.parseInt(st.nextToken());
			int tk = Integer.parseInt(st.nextToken());
			knights[t] = new Knight(t, new Node(tr, tc), th, tw, tk, false, 0);
			for (int i = tr; i < tr + th; i++) {
				for (int j = tc; j < tc + tw; j++) {
					map2[i][j] = t;
				}
			}
		}
		orders = new LinkedList<>();
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int tx = Integer.parseInt(st.nextToken());
			int ty = Integer.parseInt(st.nextToken());
			orders.offer(new Node(tx, ty));
		}
		while (!orders.isEmpty()) {
			Node order = orders.poll();
			if (knights[order.x].out)
				continue; // 아웃된 기사 명령일 때
			stack = new Stack<>();
			stack2 = new Stack<>();
			if (!checkMovable(order.x, order.y)) {
				continue;
			}
			while (!stack.isEmpty()) {
				int kn = stack.pop();
				if (kn != order.x)
					stack2.push(kn);
				move(kn, order.y);
			}
			while (!stack2.isEmpty()) {
				int kn = stack2.pop();
				int damage = findTrap(kn) * (-1);
				if (knights[kn].k - damage <= 0) {
					knights[kn].k = 0;
					knights[kn].out = true;
					for (int i = knights[kn].pos.x; i < knights[kn].pos.x + knights[kn].h; i++) {
						for (int j = knights[kn].pos.y; j < knights[kn].pos.y + knights[kn].w; j++)
							map2[i][j] = 0;
					}
				}else {
					knights[kn].damaged += damage;
				}
			}
		}
		for (int i = 1; i <= N; i++) {
			if (!knights[i].out)
				answer += knights[i].damaged;
		}
		System.out.println(answer);
	}
}