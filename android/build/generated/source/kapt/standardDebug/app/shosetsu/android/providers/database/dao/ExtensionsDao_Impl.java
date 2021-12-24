package app.shosetsu.android.providers.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import app.shosetsu.android.domain.model.database.DBInstalledExtensionEntity;
import app.shosetsu.android.domain.model.database.DBStrippedExtensionEntity;
import app.shosetsu.android.providers.database.converters.ChapterTypeConverter;
import app.shosetsu.android.providers.database.converters.ExtensionTypeConverter;
import app.shosetsu.android.providers.database.converters.VersionConverter;
import app.shosetsu.lib.ExtensionType;
import app.shosetsu.lib.Novel;
import app.shosetsu.lib.Version;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ExtensionsDao_Impl implements InstalledExtensionsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DBInstalledExtensionEntity> __insertionAdapterOfDBExtensionEntity;

  private final VersionConverter __versionConverter = new VersionConverter();

  private final ExtensionTypeConverter __extensionTypeConverter = new ExtensionTypeConverter();

  private final ChapterTypeConverter __chapterTypeConverter = new ChapterTypeConverter();

  private final EntityInsertionAdapter<DBInstalledExtensionEntity> __insertionAdapterOfDBExtensionEntity_1;

  private final EntityInsertionAdapter<DBInstalledExtensionEntity> __insertionAdapterOfDBExtensionEntity_2;

  private final EntityDeletionOrUpdateAdapter<DBInstalledExtensionEntity> __deletionAdapterOfDBExtensionEntity;

  private final EntityDeletionOrUpdateAdapter<DBInstalledExtensionEntity> __updateAdapterOfDBExtensionEntity;

  public ExtensionsDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDBExtensionEntity = new EntityInsertionAdapter<DBInstalledExtensionEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `extensions` (`id`,`repoID`,`name`,`fileName`,`imageURL`,`lang`,`version`,`md5`,`type`,`enabled`,`chapterType`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBInstalledExtensionEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getRepoID());
        if (value.getName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getName());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFileName());
        }
        if (value.getImageURL() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageURL());
        }
        if (value.getLang() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getLang());
        }
        final String _tmp = __versionConverter.toString(value.getVersion());
        if (_tmp == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, _tmp);
        }
        if (value.getMd5() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getMd5());
        }
        final int _tmp_1 = __extensionTypeConverter.toInt(value.getType());
        stmt.bindLong(9, _tmp_1);
        final int _tmp_2 = value.getEnabled() ? 1 : 0;
        stmt.bindLong(10, _tmp_2);
        final int _tmp_3 = __chapterTypeConverter.toString(value.getChapterType());
        stmt.bindLong(11, _tmp_3);
      }
    };
    this.__insertionAdapterOfDBExtensionEntity_1 = new EntityInsertionAdapter<DBInstalledExtensionEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `extensions` (`id`,`repoID`,`name`,`fileName`,`imageURL`,`lang`,`version`,`md5`,`type`,`enabled`,`chapterType`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBInstalledExtensionEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getRepoID());
        if (value.getName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getName());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFileName());
        }
        if (value.getImageURL() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageURL());
        }
        if (value.getLang() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getLang());
        }
        final String _tmp = __versionConverter.toString(value.getVersion());
        if (_tmp == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, _tmp);
        }
        if (value.getMd5() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getMd5());
        }
        final int _tmp_1 = __extensionTypeConverter.toInt(value.getType());
        stmt.bindLong(9, _tmp_1);
        final int _tmp_2 = value.getEnabled() ? 1 : 0;
        stmt.bindLong(10, _tmp_2);
        final int _tmp_3 = __chapterTypeConverter.toString(value.getChapterType());
        stmt.bindLong(11, _tmp_3);
      }
    };
    this.__insertionAdapterOfDBExtensionEntity_2 = new EntityInsertionAdapter<DBInstalledExtensionEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `extensions` (`id`,`repoID`,`name`,`fileName`,`imageURL`,`lang`,`version`,`md5`,`type`,`enabled`,`chapterType`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBInstalledExtensionEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getRepoID());
        if (value.getName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getName());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFileName());
        }
        if (value.getImageURL() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageURL());
        }
        if (value.getLang() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getLang());
        }
        final String _tmp = __versionConverter.toString(value.getVersion());
        if (_tmp == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, _tmp);
        }
        if (value.getMd5() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getMd5());
        }
        final int _tmp_1 = __extensionTypeConverter.toInt(value.getType());
        stmt.bindLong(9, _tmp_1);
        final int _tmp_2 = value.getEnabled() ? 1 : 0;
        stmt.bindLong(10, _tmp_2);
        final int _tmp_3 = __chapterTypeConverter.toString(value.getChapterType());
        stmt.bindLong(11, _tmp_3);
      }
    };
    this.__deletionAdapterOfDBExtensionEntity = new EntityDeletionOrUpdateAdapter<DBInstalledExtensionEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `extensions` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBInstalledExtensionEntity value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfDBExtensionEntity = new EntityDeletionOrUpdateAdapter<DBInstalledExtensionEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `extensions` SET `id` = ?,`repoID` = ?,`name` = ?,`fileName` = ?,`imageURL` = ?,`lang` = ?,`version` = ?,`md5` = ?,`type` = ?,`enabled` = ?,`chapterType` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBInstalledExtensionEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getRepoID());
        if (value.getName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getName());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFileName());
        }
        if (value.getImageURL() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageURL());
        }
        if (value.getLang() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getLang());
        }
        final String _tmp = __versionConverter.toString(value.getVersion());
        if (_tmp == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, _tmp);
        }
        if (value.getMd5() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getMd5());
        }
        final int _tmp_1 = __extensionTypeConverter.toInt(value.getType());
        stmt.bindLong(9, _tmp_1);
        final int _tmp_2 = value.getEnabled() ? 1 : 0;
        stmt.bindLong(10, _tmp_2);
        final int _tmp_3 = __chapterTypeConverter.toString(value.getChapterType());
        stmt.bindLong(11, _tmp_3);
        stmt.bindLong(12, value.getId());
      }
    };
  }

  @Override
  public Object insertAllReplace(final List<? extends DBInstalledExtensionEntity> list,
      final Continuation<? super Long[]> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long[]>() {
      @Override
      public Long[] call() throws Exception {
        __db.beginTransaction();
        try {
          Long[] _result = __insertionAdapterOfDBExtensionEntity.insertAndReturnIdsArrayBox(list);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertReplace(final DBInstalledExtensionEntity data,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfDBExtensionEntity.insertAndReturnId(data);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertAllIgnore(final List<? extends DBInstalledExtensionEntity> list,
      final Continuation<? super Long[]> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long[]>() {
      @Override
      public Long[] call() throws Exception {
        __db.beginTransaction();
        try {
          Long[] _result = __insertionAdapterOfDBExtensionEntity_1.insertAndReturnIdsArrayBox(list);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertIgnore(final DBInstalledExtensionEntity data,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfDBExtensionEntity_1.insertAndReturnId(data);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertAllAbort(final List<? extends DBInstalledExtensionEntity> list,
      final Continuation<? super Long[]> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long[]>() {
      @Override
      public Long[] call() throws Exception {
        __db.beginTransaction();
        try {
          Long[] _result = __insertionAdapterOfDBExtensionEntity_2.insertAndReturnIdsArrayBox(list);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertAbort(final DBInstalledExtensionEntity data,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfDBExtensionEntity_2.insertAndReturnId(data);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object delete(final DBInstalledExtensionEntity data,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDBExtensionEntity.handle(data);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object update(final DBInstalledExtensionEntity data,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDBExtensionEntity.handle(data);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public void blockingUpdate(final DBInstalledExtensionEntity data) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDBExtensionEntity.handle(data);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Flow<List<DBInstalledExtensionEntity>> loadExtensionsFlow() {
    final String _sql = "SELECT * FROM extensions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"extensions"}, new Callable<List<DBInstalledExtensionEntity>>() {
      @Override
      public List<DBInstalledExtensionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
          final List<DBInstalledExtensionEntity> _result = new ArrayList<DBInstalledExtensionEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBInstalledExtensionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpRepoID;
            _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpImageURL;
            if (_cursor.isNull(_cursorIndexOfImageURL)) {
              _tmpImageURL = null;
            } else {
              _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
            }
            final String _tmpLang;
            if (_cursor.isNull(_cursorIndexOfLang)) {
              _tmpLang = null;
            } else {
              _tmpLang = _cursor.getString(_cursorIndexOfLang);
            }
            final Version _tmpVersion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfVersion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfVersion);
            }
            _tmpVersion = __versionConverter.toVersion(_tmp);
            final String _tmpMd5;
            if (_cursor.isNull(_cursorIndexOfMd5)) {
              _tmpMd5 = null;
            } else {
              _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
            }
            final ExtensionType _tmpType;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
            final boolean _tmpEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_2 != 0;
            final Novel.ChapterType _tmpChapterType;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
            _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
            _item = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<DBInstalledExtensionEntity> loadExtensions() {
    final String _sql = "SELECT * FROM extensions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
      final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
      final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
      final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
      final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
      final List<DBInstalledExtensionEntity> _result = new ArrayList<DBInstalledExtensionEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DBInstalledExtensionEntity _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final int _tmpRepoID;
        _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpFileName;
        if (_cursor.isNull(_cursorIndexOfFileName)) {
          _tmpFileName = null;
        } else {
          _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
        }
        final String _tmpImageURL;
        if (_cursor.isNull(_cursorIndexOfImageURL)) {
          _tmpImageURL = null;
        } else {
          _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
        }
        final String _tmpLang;
        if (_cursor.isNull(_cursorIndexOfLang)) {
          _tmpLang = null;
        } else {
          _tmpLang = _cursor.getString(_cursorIndexOfLang);
        }
        final Version _tmpVersion;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfVersion)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfVersion);
        }
        _tmpVersion = __versionConverter.toVersion(_tmp);
        final String _tmpMd5;
        if (_cursor.isNull(_cursorIndexOfMd5)) {
          _tmpMd5 = null;
        } else {
          _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
        }
        final ExtensionType _tmpType;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfType);
        _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
        final boolean _tmpEnabled;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp_2 != 0;
        final Novel.ChapterType _tmpChapterType;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
        _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
        _item = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Flow<List<DBInstalledExtensionEntity>> loadEnabledExtensions() {
    final String _sql = "SELECT * FROM extensions WHERE enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"extensions"}, new Callable<List<DBInstalledExtensionEntity>>() {
      @Override
      public List<DBInstalledExtensionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
          final List<DBInstalledExtensionEntity> _result = new ArrayList<DBInstalledExtensionEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBInstalledExtensionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpRepoID;
            _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpImageURL;
            if (_cursor.isNull(_cursorIndexOfImageURL)) {
              _tmpImageURL = null;
            } else {
              _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
            }
            final String _tmpLang;
            if (_cursor.isNull(_cursorIndexOfLang)) {
              _tmpLang = null;
            } else {
              _tmpLang = _cursor.getString(_cursorIndexOfLang);
            }
            final Version _tmpVersion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfVersion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfVersion);
            }
            _tmpVersion = __versionConverter.toVersion(_tmp);
            final String _tmpMd5;
            if (_cursor.isNull(_cursorIndexOfMd5)) {
              _tmpMd5 = null;
            } else {
              _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
            }
            final ExtensionType _tmpType;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
            final boolean _tmpEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_2 != 0;
            final Novel.ChapterType _tmpChapterType;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
            _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
            _item = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DBStrippedExtensionEntity>> loadEnabledExtensionsBasic() {
    final String _sql = "SELECT id, name, imageURL FROM extensions WHERE enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"extensions"}, new Callable<List<DBStrippedExtensionEntity>>() {
      @Override
      public List<DBStrippedExtensionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfImageURL = 2;
          final List<DBStrippedExtensionEntity> _result = new ArrayList<DBStrippedExtensionEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBStrippedExtensionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpImageURL;
            if (_cursor.isNull(_cursorIndexOfImageURL)) {
              _tmpImageURL = null;
            } else {
              _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
            }
            _item = new DBStrippedExtensionEntity(_tmpId,_tmpName,_tmpImageURL);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public DBInstalledExtensionEntity getExtension(final int id) throws SQLiteException {
    final String _sql = "SELECT * FROM extensions WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
      final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
      final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
      final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
      final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
      final DBInstalledExtensionEntity _result;
      if(_cursor.moveToFirst()) {
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final int _tmpRepoID;
        _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpFileName;
        if (_cursor.isNull(_cursorIndexOfFileName)) {
          _tmpFileName = null;
        } else {
          _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
        }
        final String _tmpImageURL;
        if (_cursor.isNull(_cursorIndexOfImageURL)) {
          _tmpImageURL = null;
        } else {
          _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
        }
        final String _tmpLang;
        if (_cursor.isNull(_cursorIndexOfLang)) {
          _tmpLang = null;
        } else {
          _tmpLang = _cursor.getString(_cursorIndexOfLang);
        }
        final Version _tmpVersion;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfVersion)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfVersion);
        }
        _tmpVersion = __versionConverter.toVersion(_tmp);
        final String _tmpMd5;
        if (_cursor.isNull(_cursorIndexOfMd5)) {
          _tmpMd5 = null;
        } else {
          _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
        }
        final ExtensionType _tmpType;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfType);
        _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
        final boolean _tmpEnabled;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp_2 != 0;
        final Novel.ChapterType _tmpChapterType;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
        _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
        _result = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Flow<DBInstalledExtensionEntity> getExtensionFlow(final int id) {
    final String _sql = "SELECT * FROM extensions WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"extensions"}, new Callable<DBInstalledExtensionEntity>() {
      @Override
      public DBInstalledExtensionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
          final DBInstalledExtensionEntity _result;
          if(_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpRepoID;
            _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpImageURL;
            if (_cursor.isNull(_cursorIndexOfImageURL)) {
              _tmpImageURL = null;
            } else {
              _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
            }
            final String _tmpLang;
            if (_cursor.isNull(_cursorIndexOfLang)) {
              _tmpLang = null;
            } else {
              _tmpLang = _cursor.getString(_cursorIndexOfLang);
            }
            final Version _tmpVersion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfVersion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfVersion);
            }
            _tmpVersion = __versionConverter.toVersion(_tmp);
            final String _tmpMd5;
            if (_cursor.isNull(_cursorIndexOfMd5)) {
              _tmpMd5 = null;
            } else {
              _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
            }
            final ExtensionType _tmpType;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
            final boolean _tmpEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_2 != 0;
            final Novel.ChapterType _tmpChapterType;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
            _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
            _result = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public int getCount(final int formatterID) throws SQLiteException {
    final String _sql = "SELECT COUNT(*) FROM extensions WHERE id= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, formatterID);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DBInstalledExtensionEntity> getExtensions(final int repoID) {
    final String _sql = "SELECT * FROM extensions WHERE repoID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, repoID);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRepoID = CursorUtil.getColumnIndexOrThrow(_cursor, "repoID");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
      final int _cursorIndexOfImageURL = CursorUtil.getColumnIndexOrThrow(_cursor, "imageURL");
      final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
      final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
      final int _cursorIndexOfMd5 = CursorUtil.getColumnIndexOrThrow(_cursor, "md5");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final int _cursorIndexOfChapterType = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterType");
      final List<DBInstalledExtensionEntity> _result = new ArrayList<DBInstalledExtensionEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DBInstalledExtensionEntity _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final int _tmpRepoID;
        _tmpRepoID = _cursor.getInt(_cursorIndexOfRepoID);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpFileName;
        if (_cursor.isNull(_cursorIndexOfFileName)) {
          _tmpFileName = null;
        } else {
          _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
        }
        final String _tmpImageURL;
        if (_cursor.isNull(_cursorIndexOfImageURL)) {
          _tmpImageURL = null;
        } else {
          _tmpImageURL = _cursor.getString(_cursorIndexOfImageURL);
        }
        final String _tmpLang;
        if (_cursor.isNull(_cursorIndexOfLang)) {
          _tmpLang = null;
        } else {
          _tmpLang = _cursor.getString(_cursorIndexOfLang);
        }
        final Version _tmpVersion;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfVersion)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfVersion);
        }
        _tmpVersion = __versionConverter.toVersion(_tmp);
        final String _tmpMd5;
        if (_cursor.isNull(_cursorIndexOfMd5)) {
          _tmpMd5 = null;
        } else {
          _tmpMd5 = _cursor.getString(_cursorIndexOfMd5);
        }
        final ExtensionType _tmpType;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfType);
        _tmpType = __extensionTypeConverter.toExtensionType(_tmp_1);
        final boolean _tmpEnabled;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp_2 != 0;
        final Novel.ChapterType _tmpChapterType;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfChapterType);
        _tmpChapterType = __chapterTypeConverter.toSortType(_tmp_3);
        _item = new DBInstalledExtensionEntity(_tmpId,_tmpRepoID,_tmpName,_tmpFileName,_tmpImageURL,_tmpLang,_tmpVersion,_tmpMd5,_tmpType,_tmpEnabled,_tmpChapterType);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public boolean doesExtensionExist(final int formatterID) throws SQLiteException {
    return InstalledExtensionsDao.DefaultImpls.doesExtensionExist(ExtensionsDao_Impl.this, formatterID);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
