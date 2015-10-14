package im.summerhao.com.myapplication.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/** 文件工具类 */
public class FileUtil {
	private static final String TAG = "FileUtil";

	public static void copyFile(File fromFile, String toFile)
			throws IOException {

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
		}
	}

	public static File createNewFile(File file) {

		try {
			if (file.exists()) {
				return file;
			}
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			Log.e(TAG, "", e);
			return null;
		}
		return file;
	}

	public static File createNewFile(String path) {
		File file = new File(path);
		return createNewFile(file);
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		deleteFile(file);
	}

	public static void deleteFile(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File files[] = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		file.delete();
	}

	public static boolean write(String path, String content) {
		return write(path, content, false);
	}

	public static boolean write(String path, String content, boolean append) {
		return write(new File(path), content, append);
	}

	public static boolean write(File file, String content) {
		return write(file, content, false);
	}

	public static boolean write(File file, String content, boolean append) {
		if (file == null || StringUtil.empty(content)) {
			return false;
		}
		if (!file.exists()) {
			file = createNewFile(file);
		}
		FileOutputStream ops = null;
		try {
			ops = new FileOutputStream(file, append);
			ops.write(content.getBytes());
		} catch (Exception e) {
			Log.e(TAG, "", e);
			return false;
		} finally {
			try {
				ops.close();
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}
			ops = null;
		}

		return true;
	}

	public static String getFileName(String path) {
		if (StringUtil.empty(path)) {
			return null;
		}
		File f = new File(path);
		String name = f.getName();
		f = null;
		return name;
	}

	@SuppressWarnings("resource")
	public static List<String> readFile(File file, int startLine, int lineCount) {
		if (file == null || startLine < 1 || lineCount < 1) {
			return null;
		}
		if (!file.exists()) {
			return null;
		}
		FileReader fileReader = null;
		List<String> list = null;
		try {
			list = new ArrayList<String>();
			fileReader = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fileReader);
			boolean end = false;
			for (int i = 1; i < startLine; i++) {
				if (lnr.readLine() == null) {
					end = true;
					break;
				}
			}
			if (end == false) {
				for (int i = startLine; i < startLine + lineCount; i++) {
					String line = lnr.readLine();
					if (line == null) {
						break;
					}
					list.add(line);

				}
			}
		} catch (Exception e) {
			Log.e(TAG, "read log error!", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static boolean createDir(File dir) {
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return true;
		} catch (Exception e) {
			Log.e(TAG, "create dir error", e);
			return false;
		}
	}

	public static File creatSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdir();
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = createNewFile(path + "/" + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			int len = -1;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String readFile(File file) {
		Reader read = null;
		String content = "";
		String string = "";
		BufferedReader br = null;
		try {
			read = new FileReader(file);
			br = new BufferedReader(read);
			while ((content = br.readLine().toString().trim()) != null) {
				string += content + "\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				read.close();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("string=" + string);
		return string.toString();
	}

}
