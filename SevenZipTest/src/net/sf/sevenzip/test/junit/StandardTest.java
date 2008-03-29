package net.sf.sevenzip.test.junit;

import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.zip.ZipFile;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.impl.InStreamImpl;
import net.sf.sevenzip.test.ZipContentComparator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class StandardTest {
	private static void reloadLibPath() throws Exception {
		// Reset the "sys_paths" field of the ClassLoader to null.
		Class<?> clazz = ClassLoader.class;
		Field field = clazz.getDeclaredField("sys_paths");
		boolean accessible = field.isAccessible();
		if (!accessible)
			field.setAccessible(true);
		// Object original = field.get(clazz);
		// Reset it to null so that whenever "System.loadLibrary" is called, it
		// will be reconstructed with the changed value.
		field.set(clazz, null);
	}

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("java.library.path", "..\\SevenZipCPP\\Release\\");
		reloadLibPath();
	}

	protected abstract int getTestId();

	@Test
	public void simpleTest_7z() throws Exception {
		test(ArchiveFormat.SEVEN_ZIP, "7z");
	}

	@Test
	public void simpleTest_zip() throws Exception {
		test(ArchiveFormat.ZIP, "zip");
	}

	// @Test
	// public void simpleTest_tar() throws Exception {
	// test(ArchiveFormat.TAR, "tar");
	// }

	private void test(ArchiveFormat archiveFormat, String format)
			throws Exception {
		int testId = getTestId();
		ZipFile zipFile = new ZipFile("ArchiveContents/ArchiveContent" + testId
				+ ".zip");
		IInArchive sevenZipArchive = SevenZip.openInArchive(archiveFormat,
				new InStreamImpl(
						new RandomAccessFile("TestArchives/TestArchive"
								+ testId + "." + format, "r")));

		ZipContentComparator contentComparator = new ZipContentComparator(
				sevenZipArchive, zipFile);
		Assert.assertTrue(contentComparator.getErrorMessage(),
				contentComparator.isEqual());

	}
}
