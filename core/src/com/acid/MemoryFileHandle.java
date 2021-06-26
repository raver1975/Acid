package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public class MemoryFileHandle extends FileHandle {
    private String fileName = null;
    public ByteArray ba = new ByteArray();
    //    public ArrayMap<String,MemoryFileHandle> siblings=new ArrayMap<>();
    public ArrayMap<String, MemoryFileHandle> children = new ArrayMap<>();
    MemoryFileHandle parent;

//	public MemoryFileHandle(ZipFile archive, File file) {
//		super(file, FileType.Classpath);
//		this.archive = archive;
//		archiveEntry = this.archive.getEntry(file.getPath());
//	}

    public MemoryFileHandle(byte[] b) {
        ba.addAll(b);
        this.fileName = MathUtils.random.nextInt()+"";
    }




    public MemoryFileHandle(String fileName) {
        this.fileName = fileName;
//        super(fileName.replace('\\', '/'), FileType.Local);
//		this.archive = archive;
//		this.archiveEntry = archive.getEntry(fileName.replace('\\', '/'));
    }

    public MemoryFileHandle() {
        this.fileName = MathUtils.random.nextInt()+"";;
    }

    @Override
    public String name() {
        return fileName;
    }

    @Override
    public Writer writer(boolean append) {
        if (!append) {
            ba.clear();
        }
        return new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                for (int i = off; i < off + len; i++) {
                    ba.add((byte) cbuf[i]);
                }
            }

            @Override
            public void flush() throws IOException {

            }

            @Override
            public void close() throws IOException {

            }
        };
    }

    @Override
    public MemoryFileHandle sibling(String name) {
        if (children.containsKey(name)) {
            return children.get(name);
        } else {
            MemoryFileHandle mfh = new MemoryFileHandle(name);
            children.put(name, mfh);
            return mfh;
        }
    }

    @Override
    public MemoryFileHandle child(String name) {
        if (children.containsKey(name)) {
            return children.get(name);
        } else {
            MemoryFileHandle mfh = new MemoryFileHandle(name);
            children.put(name, mfh);
            return mfh;
        }
    }
//	@Override
//	public FileHandle child (String name) {
//		name = name.replace('\\', '/');
//		if (file.getPath().length() == 0) return new MemoryFileHandle(archive, new File(name));
//		return new MemoryFileHandle(archive, new File(file, name));
//	}

//	@Override
//	public FileHandle sibling (String name) {
//		name = name.replace('\\', '/');
//		if (file.getPath().length() == 0) throw new GdxRuntimeException("Cannot get the sibling of the root.");
//		return new MemoryFileHandle(archive, new File(file.getParent(), name));
//	}

//	@Override
//	public FileHandle parent () {
//		File parent = file.getParentFile();
//		if (parent == null) {
//			if (type == FileType.Absolute)
//				parent = new File("/");
//			else
//				parent = new File("");
//		}
//		return new MemoryFileHandle(archive, parent);
//	}


    @Override
    public OutputStream write(boolean append) {
//        System.out.println("writing");
        if (!append) {
            ba.clear();
        }
        return new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                ba.addAll(b, off, len);
            }

            @Override
            public void write(int b) throws IOException {
                ba.add((byte) b);
            }
        };
    }

    @Override
    public InputStream read() {
//        System.out.println("reading");
        return new InputStream() {
            int cnt = 0;

            @Override
            public int read() throws IOException {
                if (cnt >= ba.size) {
                    return -1;
                }
                return ba.get(cnt++) & 0xff;
            }
        };
    }

    public void write(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = write(append);
            StreamUtils.copyStream(input, output);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
        } finally {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }

    }

//	@Override
//	public boolean exists() {
//		return archiveEntry != null;
//	}

    @Override
    public long length() {
        return ba.size;
    }

//	@Override
//	public long lastModified () {
//		return archiveEntry.getTime();
//	}

    @Override
    public byte[] readBytes() {
        return ba.toArray();
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        if (!append) ba.clear();
        ba.addAll(bytes);
    }

    @Override
    public String toString() {
        return "Memory file:" + ba.size;
    }
}