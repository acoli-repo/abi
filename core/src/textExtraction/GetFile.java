package textExtraction;

import java.io.File;
import java.io.FilenameFilter;

public class GetFile {

	public static FilenameFilter filter(final String type) {
		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String path) {
				String filename = new File(path).getName();
				return filename.indexOf(type) != -1;
			};

		};
	}

	public String[] getPDFfile(String filepath) {
		File file = new File(filepath);
		String filenames[];
		filenames = file.list(filter(".pdf"));
		return filenames;
	}
}
