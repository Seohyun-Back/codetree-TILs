package practice2;

import java.util.*;
import java.io.*;

public class Main {
	static int N, M, P, C, D;
	static int[][] map;
	static Santa[] santas;
	static Node rp;
	static int[] rdx = { 1, 1, 1, 0, -1, -1, -1, 0 }; // 루돌프 이동 방향
	static int[] rdy = { 1, 0, -1, -1, -1, 0, 1, 1 };
	static int[] dx = { -1, 0, 1, 0 }; // 산타 이동 방향
	static int[] dy = { 0, 1, 0, -1 };

	public static class Santa {
		int n;
		Node pos;
		int faint;
		int score;
		boolean out;

		public Santa(int n, Node pos, int faint, int score, boolean out) {
			this.n = n;
			this.pos = pos;
			this.faint = faint;
			this.score = score;
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

	public static int getDir(int minIndex) {
		Node sp = santas[minIndex].pos;
		if (rp.x < sp.x) {
			if (rp.y < sp.y)
				return 0;
			if (rp.y == sp.y)
				return 1;
			if (rp.y > sp.y)
				return 2;
		} else if (rp.x == sp.x) {
			if (rp.y < sp.y)
				return 7;
			if (rp.y == sp.y) {
				System.out.println("r, s, same position error");
				return 8;
			}
			if (rp.y > sp.y)
				return 3;
		} else if (rp.x > sp.x) {
			if (rp.y < sp.y)
				return 6;
			if (rp.y == sp.y)
				return 5;
			if (rp.y > sp.y)
				return 4;
		}
		return -1;
	}

	public static int getDir2(int n) {
		Santa s = santas[n];
		for (int i = 0; i < 4; i++) {
			if (s.pos.x + dx[i] <= 0 || s.pos.x + dx[i] > N || s.pos.y + dy[i] <= 0 && s.pos.y + dy[i] > N)
				continue;
			if (map[s.pos.x + dx[i]][s.pos.y + dy[i]] >= 1)
				continue;
			if (i == 0 && s.pos.x > rp.x)
				return 0;
			else if (i == 1 && s.pos.y < rp.y)
				return 1;
			else if (i == 2 && s.pos.x < rp.x)
				return 2;
			else if (i == 3 && s.pos.y > rp.y)
				return 3;
		}
		return -1;
	}

	public static void interaction(int minIndex, int t) {
		// 상호작용
		Queue<Santa> q = new LinkedList<>();
		q.offer(santas[map[santas[minIndex].pos.x][santas[minIndex].pos.y]]);
		map[santas[minIndex].pos.x][santas[minIndex].pos.y] = minIndex;
		while (!q.isEmpty()) {
			Santa s = q.poll();
			santas[s.n].pos.x += rdx[t];
			s.pos.x += rdx[t];
			santas[s.n].pos.y += rdy[t];
			s.pos.y += rdy[t];
			if (s.pos.x <= 0 || s.pos.x > N || s.pos.y <= 0 || s.pos.y > N) {
				santas[s.n].out = true;
				continue;
			}
			if (map[s.pos.x][s.pos.y] >= 1) {
				q.offer(santas[map[s.pos.x][s.pos.y]]);
			}
			map[s.pos.x][s.pos.y] = s.n;
		}
	}

	public static void play(int turn) {
		if (turn > M)
			return;
		// 거리 갱신
		int minDist = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 1; i <= P; i++) {
			if (santas[i].out) {
				if (i == P && minDist == Integer.MAX_VALUE)
					return;
				continue;
			}
			if (((santas[i].pos.x - rp.x) * (santas[i].pos.x - rp.x))
					+ ((santas[i].pos.y - rp.y) * (santas[i].pos.y - rp.y)) < minDist) {
				minDist = ((santas[i].pos.x - rp.x) * (santas[i].pos.x - rp.x))
						+ ((santas[i].pos.y - rp.y) * (santas[i].pos.y - rp.y));
				minIndex = i;
			}
		}
		// 루돌프 움직임
		int t = getDir(minIndex);
		map[rp.x][rp.y] = 0;
		rp.x += rdx[t];
		rp.y += rdy[t];

		// 루돌프 충돌
		if (map[rp.x][rp.y] >= 1) {
			map[rp.x][rp.y] = 1;
			santas[minIndex].score += C;
			santas[minIndex].pos.x += (rdx[t] * C);
			santas[minIndex].pos.y += (rdy[t] * C);
			if (santas[minIndex].pos.x <= 0 || santas[minIndex].pos.x > N || santas[minIndex].pos.y <= 0
					|| santas[minIndex].pos.x > N) {
				santas[minIndex].out = true;
			} else if (map[santas[minIndex].pos.x][santas[minIndex].pos.y] >= 1) {
				if (santas[minIndex].faint == 0)
					santas[minIndex].faint = 1;
				// 상호작용
				interaction(minIndex, t);
			} else { // 밀려나감
				map[santas[minIndex].pos.x][santas[minIndex].pos.y] = minIndex;
				if (santas[minIndex].faint == 0)
					santas[minIndex].faint = 1;
			}
		}
		// 산타 움직임
		for (int i = 1; i <= P; i++) {
			if (santas[i].out || santas[i].faint > 0) {
				continue;
			}
			t = getDir2(i);
			if (t == -1)
				continue;
			santas[i].pos.x += dx[t];
			santas[i].pos.y += dy[t];

			// 루돌프 충돌
			if (map[santas[i].pos.x][santas[i].pos.y] == -1) {
				santas[i].score += D;
				t = (t - 2 < 0) ? t + 2 : t - 2;
				santas[i].pos.x += (dx[t] * D);
				santas[i].pos.y += (dx[t] * D);
			} // 상호작용
		}

		// 기절,점수 갱신
		for (int i = 1; i <= P; i++) {
			if (santas[i].faint == 1)
				santas[minIndex].faint = 2;
			else if (santas[i].faint == 2)
				santas[minIndex].faint = 0;
			if (!santas[i].out)
				santas[i].score++;
		}
		turn++;
	}

	public static void main(String[] args) throws IOException {
		// 여기에 코드를 작성해주세요.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		map = new int[N + 1][N + 1];
		santas = new Santa[P + 1];
		st = new StringTokenizer(br.readLine());
		int tx = Integer.parseInt(st.nextToken());
		int ty = Integer.parseInt(st.nextToken());
		rp = new Node(tx, ty);
		map[rp.x][rp.y] = -1;
		for (int i = 1; i <= P; i++) {
			st = new StringTokenizer(br.readLine());
			int tn = Integer.parseInt(st.nextToken());
			tx = Integer.parseInt(st.nextToken());
			ty = Integer.parseInt(st.nextToken());
			santas[i] = new Santa(tn, new Node(tx, ty), 0, 0, false);
			map[tx][ty] = tn;
		}

		play(1);
		System.out.println("Hello");

	}
}