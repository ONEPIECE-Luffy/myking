package com.kingdeehit.mobile.his.xianggang.service.utilhl7;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

public class ConnectionPoolFactory {

	private GenericObjectPool<Socket> pool;
	protected Logger logger = Logger.getLogger(getClass());
//	public static Socket socket;
	public ConnectionPoolFactory(GenericObjectPoolConfig config, String ip, int port) {
		ConnectionFactory factory = new ConnectionFactory(ip, port);
		pool = new GenericObjectPool<Socket>(factory, config);
	}

	public Socket getConnection() throws Exception {
		return pool.borrowObject();
	}

	public void releaseConnection(Socket socket) {
		try {
			logger.debug("正在归还returnObject_socket："+socket);
			pool.returnObject(socket);
		} catch (Exception e) {
			logger.debug("归还链接releaseConnection异常："+e);
			/*if (socket != null) {
				try {
					socket.close();
				} catch (Exception ex) {

				}
			}*/
		}
	}

	/**
	 * inner
	 *
	 */
	class ConnectionFactory extends BasePooledObjectFactory<Socket> {

		private InetSocketAddress address;
		private Socket socket;

		public ConnectionFactory(String ip, int port) {
			address = new InetSocketAddress(ip, port);
		}

		@Override
		public Socket create() throws Exception {
			logger.debug("正在创建socket");
			Socket socket = new Socket();
			socket.connect(address);
			return socket;

			/*try {
				logger.debug("socket:" + socket);
				logger.debug("是否关闭socket_isClosed:" + socket.isClosed());
			} catch (Exception e) {
				logger.error("查询socket状态异常",e);
			}

			if (socket!=null&&!socket.isClosed()) {
				logger.debug("继续沿用socket");
				return socket;
			}else {
				logger.debug("正在创建socket");
				socket = new Socket();
//				socket.setKeepAlive(true);
				socket.connect(address);
				return socket;
			}*/
		}

		@Override
		public PooledObject<Socket> wrap(Socket obj) {
			/*Socket socket = new Socket();
			try {
				socket.connect(address);
				logger.info("socket_wrap:"+socket);
			} catch (IOException e) {
				logger.error("socket连接失败", e);
			}*/

			if (socket!=null&&!socket.isClosed()) {
				return new DefaultPooledObject<Socket>(socket);
			}else {
				socket = new Socket();
				try {
//					socket.setKeepAlive(true);
					socket.connect(address);
					logger.info("socket_wrap:"+socket);
				} catch (IOException e) {
					logger.error("socket连接失败", e);
				}
				return new DefaultPooledObject<Socket>(socket);
			}
		}

		@Override
		public void destroyObject(PooledObject<Socket> p) throws Exception {
			Socket socket = p.getObject();
			//logger.debug("是否关闭前destroyObject_socket_isClosed:" + socket.isClosed());
			socket.close();
			//logger.debug("是否关闭后destroyObject_socket_isClosed:" + socket.isClosed());
		}

		@Override
		public boolean validateObject(PooledObject<Socket> p) {
			Socket socket = p.getObject();
			try {
				socket.sendUrgentData(0xFF);
			} catch (IOException e) {
				return false;
			}
			return true;

		}
	}
}
