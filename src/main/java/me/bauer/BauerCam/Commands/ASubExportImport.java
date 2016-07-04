package me.bauer.BauerCam.Commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.bauer.BauerCam.Main;
import me.bauer.BauerCam.Utils;
import me.bauer.BauerCam.Path.PathHandler;
import me.bauer.BauerCam.Path.Position;

public abstract class ASubExportImport implements ISubCommand {

	private static final String extension = ".txt";

	public void save(final String fileName) {
		final Position[] points = PathHandler.getWaypoints();
		if (points.length == 0) {
			Utils.sendInformation(Main.pathIsEmpty.toString());
			return;
		}

		final File file = new File(Main.bauercamDirectory, fileName + extension);

		try {
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
			for (final Position point : points) {
				writer.write(point.toString());
				writer.newLine();
			}
			writer.close();
		} catch (final IOException e) {
			Utils.sendInformation(Main.IOError.toString());
			Utils.sendInformation(e.getMessage());
			return;
		}

		Utils.sendInformation(Main.exportSuccessful.toString());
	}

	public void load(final String fileName) {
		final File file = new File(Main.bauercamDirectory, fileName + extension);

		if (!file.exists() || !file.isFile()) {
			Utils.sendInformation(Main.fileDoesNotExist.toString());
			return;
		}

		final ArrayList<Position> points = new ArrayList<Position>();

		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			String s;
			while ((s = reader.readLine()) != null) {
				final Position point = Position.fromString(s);
				if (point == null) {
					reader.close();
					return;
				}
				points.add(point);
			}

			reader.close();
		} catch (final IOException e) {
			Utils.sendInformation(Main.IOError.toString());
			Utils.sendInformation(e.getMessage());
			return;
		}

		PathHandler.setWaypoints(points);
		Utils.sendInformation(Main.importSuccessful.toString());
	}

}