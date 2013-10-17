/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/ff_style/Downloads/__doc/youplayer1/src/com/talent/allshare/server/IShareService.aidl
 */
package com.talent.allshare.server;
public interface IShareService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.talent.allshare.server.IShareService
{
private static final java.lang.String DESCRIPTOR = "com.talent.allshare.server.IShareService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.talent.allshare.server.IShareService interface,
 * generating a proxy if needed.
 */
public static com.talent.allshare.server.IShareService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.talent.allshare.server.IShareService))) {
return ((com.talent.allshare.server.IShareService)iin);
}
return new com.talent.allshare.server.IShareService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_start:
{
data.enforceInterface(DESCRIPTOR);
this.start();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_restart:
{
data.enforceInterface(DESCRIPTOR);
this.restart();
reply.writeNoException();
return true;
}
case TRANSACTION_updateconfig:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _arg4;
_arg4 = (0!=data.readInt());
this.updateconfig(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_isStarted:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isStarted();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurDevName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurDevName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getbsharevideo:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getbsharevideo();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getbsharemusic:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getbsharemusic();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getbsharepicture:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getbsharepicture();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getbsharedoc:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getbsharedoc();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.talent.allshare.server.IShareService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void start() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_start, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void restart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_restart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateconfig(java.lang.String devname, boolean bsharevideo, boolean bsharemusic, boolean bsharepicture, boolean sharedoc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(devname);
_data.writeInt(((bsharevideo)?(1):(0)));
_data.writeInt(((bsharemusic)?(1):(0)));
_data.writeInt(((bsharepicture)?(1):(0)));
_data.writeInt(((sharedoc)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateconfig, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isStarted() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isStarted, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getCurDevName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurDevName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getbsharevideo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getbsharevideo, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getbsharemusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getbsharemusic, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getbsharepicture() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getbsharepicture, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getbsharedoc() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getbsharedoc, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_start = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_restart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_updateconfig = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isStarted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getCurDevName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getbsharevideo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getbsharemusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getbsharepicture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getbsharedoc = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public void start() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void restart() throws android.os.RemoteException;
public void updateconfig(java.lang.String devname, boolean bsharevideo, boolean bsharemusic, boolean bsharepicture, boolean sharedoc) throws android.os.RemoteException;
public boolean isStarted() throws android.os.RemoteException;
public java.lang.String getCurDevName() throws android.os.RemoteException;
public boolean getbsharevideo() throws android.os.RemoteException;
public boolean getbsharemusic() throws android.os.RemoteException;
public boolean getbsharepicture() throws android.os.RemoteException;
public boolean getbsharedoc() throws android.os.RemoteException;
}
