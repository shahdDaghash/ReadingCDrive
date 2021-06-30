import java.io.File;

public class FileNode {

	private File file;
    private String filename;
    
    public FileNode(File file) {
        this.file = file;
        this.filename = file.getName();
    }
    

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	 @Override
     public String toString() {
         String name =this.getFilename();
         if (name.equals("")) {
             return file.getAbsolutePath();
         } else {
             return name;
         }
     }
}
