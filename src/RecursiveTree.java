/*
 * (C) 2020 Pascal Devenoge
 */

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import gui.Window;

import static java.lang.Math.*;

public class RecursiveTree {

	private static final double MINIMAL_SEGMENT_LENGTH = 10;

	private static final Window window = new Window("Recursive tree", 1900, 1000);
	private static final Random random = new Random();
	
	private static boolean randomize = false;
	private static boolean renderLive = false;
	
	private static long segmentCount = 0;
	private static long leafCount = 0;
	private static long leafDepthAccumulator = 0;
	private static int[] lastDepths = new int[10];
	private static int depthArrayIndex = 0;

	public static void main(String[] args) {
		setup();
		window.open();
		
		renderTree(950, 1000, PI / 2, 150, 5, 0);
		
		displayStats();
		window.refresh();
		System.out.println("Rendering complete");
		
		window.waitUntilClosed();
	}

	/**
	 * Recursively render a subtree.
	 */
	private static void renderTree(double startX, double startY, double angle, double length, double width, int depth) {
		double endX = startX + cos(angle) * length;
		double endY = startY - sin(angle) * length;
		
		renderSegmentActive(startX, startY, endX, endY);
		updateSegmentCounter();
		updateDepthCounter(depth);
				
		if (length < MINIMAL_SEGMENT_LENGTH) {
			renderLeaf(endX, endY);
			updateLeafCounter(depth);
		} else {
			double nextWidth = max(width * 0.85, 1);
			renderTree(endX, endY, angle + PI / 7 * generateRandomFactor(), 0.75 * length * generateRandomFactor(), nextWidth, depth + 1);
			renderTree(endX, endY, angle * generateRandomFactor(), 0.62 * length * generateRandomFactor(), nextWidth, depth + 1);
			renderTree(endX, endY, angle - PI / 6 * generateRandomFactor(), 0.68 * length * generateRandomFactor(), nextWidth, depth + 1);
		}
		renderSegmentPassive(startX, startY, endX, endY, width);
	}
	
	/**
	 * Draw a passive segment in a gray color.
	 * 
	 * A segment is passive if it is not part of the subtree currently being rendered.
	 */
	private static void renderSegmentPassive(double x1, double y1, double x2, double y2, double width) {
		renderSegment(x1, y1, x2, y2, 6, 50, 50, 50);
		renderSegment(x1, y1, x2, y2, width, 255, 255, 255);
	}
	
	/**
	 * Draw an active segment in a purple color.
	 * 
	 * A segment is active if it is part of the subtree currently being rendered.
	 */
	private static void renderSegmentActive(double x1, double y1, double x2, double y2) {
		renderSegment(x1, y1, x2, y2, 5, 255, 50, 150);
	}
	
	/**
	 * Render a single tree segment between points (x1, y1) and (x2, y2), with a given width and a given color
	 */
	private static void renderSegment(double x1, double y1, double x2, double y2, double width, int red, int green, int blue) {
		window.setColor(red, green, blue);
		window.setStrokeWidth(width);
		window.drawLine(x1, y1, x2, y2);
		if (renderLive) window.refresh();
	}

	/**
	 * Draw a new tree lead at a given position, varying the green color randomly,
	 * and occasionally switching to a red color.
	 */
	private static void renderLeaf(double x, double y) {
		if (random.nextInt(100) < 5) {
			window.setColor(255, 20, 0);
		} else {
			window.setColor(20, (int)(round(200 * generateRandomFactor())), 0);
		}
		window.fillCircle(x, y, 5);
		if (renderLive) window.refresh();
	}
	
	private static void updateSegmentCounter() {
		window.setColor(50, 50, 50);
		window.fillRect(0, 0, 1900, 15);
		window.setColor(255, 255, 255);
		window.drawString("Segments drawn:  " + segmentCount++, 10, 15);
		if (renderLive) window.refresh();
	}
	
	private static void updateLeafCounter(int depth) {
		window.setColor(50, 50, 50);
		window.fillRect(0, 15, 1900, 15);
		window.setColor(255, 255, 255);
		window.drawString("Leaf drawn:  " + leafCount++, 10, 30);
		if (renderLive) window.refresh();
		leafDepthAccumulator += depth;
	}
	
	/**
	 * Calculate a running average of the tree depth of the
	 * last 10 segments drawn, and display the result in the main window.
	 */
	private static void updateDepthCounter(int depth) {
		depthArrayIndex = (depthArrayIndex + 1) % 10;
		lastDepths[depthArrayIndex] = depth;
		int average = Arrays.stream(lastDepths).sum() / 10;
		
		window.setColor(50, 50, 50);
		window.fillRect(0, 30, 1900, 15);
		window.setColor(255, 255, 255);
		window.drawString("Current average depth:  " + average, 10, 45);
		if (renderLive) window.refresh();
	}
	
	private static void displayStats() {
		window.setColor(50, 50, 50);
		window.fillRect(0, 45, 1900, 30);
		window.setColor(255, 255, 255);
		window.drawString("Segments per leafs:  " + segmentCount / (double)leafCount, 10, 60);
		window.drawString("Average leaf depth:  " + leafDepthAccumulator / (double)leafCount, 10, 75);
		if (renderLive) window.refresh();
	}
	
	/**
	 * Generate random double with value distributed around 1.0
	 * If randomize is false, this method will always return exactly 1.0,
	 * thus diabling any randomization.
	 */
	private static double generateRandomFactor() {
		return randomize ? (random.nextDouble() - 0.5) / 1.2 + 1.0 : 1.0;
	}
	
	private static void setup() {
		window.setColor(50, 50, 50);
		window.fillRect(0, 0, 1900, 1000);
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Randomize? [Y] ");
			randomize = scanner.next().equalsIgnoreCase("Y");
			System.out.print("Render live? [Y] ");
			renderLive = scanner.next().equalsIgnoreCase("Y");
		}
	}
}
