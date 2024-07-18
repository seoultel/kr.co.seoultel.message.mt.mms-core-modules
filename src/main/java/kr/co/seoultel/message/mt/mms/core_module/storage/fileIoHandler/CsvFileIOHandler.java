package kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler;

public abstract class CsvFileIOHandler {
    abstract void writeOne();
    abstract void writeAll();
    abstract void readOne();
    abstract void readAll();
}
