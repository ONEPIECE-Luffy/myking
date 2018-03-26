/*package com.kingdeehit.mobile.his.xianggang.service.utilhl7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.utils.StringUtil;

public class HL7SendThread extends Thread {

	*//**
	 * 重发睡眠时间(单位：毫秒)
	 *//*
	private final int SLEEP_TIME = 2000;

	*//**
	 * 重发次数
	 *//*
	private final int REPEAT_TIMES = 3;

	protected Logger logger = Logger.getLogger(getClass());

	private ConcurrentLinkedQueue<String> sendQueue = new ConcurrentLinkedQueue<String>();
	private ConcurrentHashMap<String, String> resultQueue = new ConcurrentHashMap<String, String>();
	private ExecutorService cachedThreadPool = Executors.newFixedThreadPool(50);
	private ListeningExecutorService guavaExecutor = null;
	private ConnectionPoolFactory socketPool;

	public static final String IP_ADDR = "127.0.0.1";// 服务器地址
	public static final int PORT = 7788;// 服务器端口号
	private boolean running = true;
	private static HL7SendThread instance;

	public static synchronized HL7SendThread getInstance() {
		if (instance == null) {
			instance = new HL7SendThread();
			instance.start();
		}
		return instance;
	}

	public void shutdown() {
		running = false;
		logger.info("closing send thread......");
		try {
			cachedThreadPool.shutdownNow();
			this.interrupt();
			this.join();
		} catch (Exception e) {

		}
		logger.info("send thread closed......");
	}

	public HL7SendThread() {
		guavaExecutor = MoreExecutors.listeningDecorator(cachedThreadPool);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(1);
		//对拿到的connection进行validateObject校验
		config.setTestOnBorrow(true);
		//在进行returnObject对返回的connection进行validateObject校验
		config.setTestOnReturn(false);
		//定时对线程池中空闲的链接进行validateObject校验
		config.setTestWhileIdle(true);
		socketPool = new ConnectionPoolFactory(config, Consts.HOSPITAL_HL7_SERVER_IP, Consts.HOSPITAL_HL7_SERVER_PORT);
		//logger.info("链接创建socketPool："+"ipy与端口号："+Consts.HOSPITAL_HL7_SERVER_IP+":"+Consts.HOSPITAL_HL7_SERVER_PORT);
		this.setName("HL7SendThread");
		running = true;
		//socketPool = new ConnectionPoolFactory(config, IP_ADDR, PORT);
	}

	public void run() {
		logger.info("hl7 running start........");
		logger.info("instance_isAlive运行状态:"+instance.isAlive());
		logger.info("instance_isInterrupted中断状态:"+instance.isInterrupted());
		while (true) {
			try {
				String message = this.getMessageFromSendQueue();
				ListenableFuture<String> nativeFuture = guavaExecutor.submit(new HL7Sender(message));
				Futures.addCallback(nativeFuture, new FutureCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if (StringUtils.isEmpty(result)) {
							return;
						}
						logger.debug("message send successs.......");
						try {
							String uid = NewHL7Util.getMsgSourceId(result);
							logger.info("run-uid:"+uid);
							if (StringUtils.isNotEmpty(uid)) {
								synchronized (resultQueue) {
									logger.info("resultQueue.put.uid:"+uid);
									resultQueue.put(uid, result);
									logger.info("resultQueue.get.result:"+resultQueue.get(uid));
									resultQueue.notifyAll();
								}
							}
						} catch (Exception e) {

						}
					}
					@Override
					public void onFailure(Throwable t) {
					}
				});
			} catch (Exception e) {
				logger.error("发送消息异常", e);
				continue;
			}
		}
	}

	private class HL7Sender implements Callable<String> {

		private String message;

		public HL7Sender(String message) {
			this.message = message;
		}

		@Override
		public String call() throws Exception {
			logger.debug("发送消息:" + message);
			String result = null;
			DataOutputStream out = null;
			DataInputStream input = null;
			Socket socket = null;
			int sendTimes = 0;
			try {

				try {
//					try {
//						logger.debug("ConnectionPoolFactory.socket:" + ConnectionPoolFactory.socket);
//						logger.debug("ConnectionPoolFactory.socket_isClosed:" + ConnectionPoolFactory.socket.isClosed());
//					} catch (Exception e) {
//						logger.error("查询socket状态异常",e);
//					}
//
//					if (ConnectionPoolFactory.socket!=null&&!ConnectionPoolFactory.socket.isClosed()) {
//						socket=ConnectionPoolFactory.socket;
//					}else {
						// 创建一个流套接字并将其连接到指定主机上的指定端口号
						socket = socketPool.getConnection();
//					}

				} catch (Exception e) {
					logger.error("获取连接异常重新获取1", e);
					Thread.sleep(SLEEP_TIME);
					try {
						instance = new HL7SendThread();
						instance.start();
						socket = socketPool.getConnection();
					} catch (Exception e1) {
						Thread.sleep(SLEEP_TIME);
						logger.error("获取连接异常再次获取2", e1);
						instance = new HL7SendThread();
						instance.start();
						socket = socketPool.getConnection();
					}
				}

				if (socket.isClosed()) {
					logger.debug("是否关闭socket_isClosed:" + socket.isClosed());
					socket = new Socket();
					InetSocketAddress address=new InetSocketAddress(Consts.HOSPITAL_HL7_SERVER_IP, Consts.HOSPITAL_HL7_SERVER_PORT);
					socket.connect(address);
//					ConnectionPoolFactory.socket=socket;
					logger.debug("是否开启socket_isClosed:" + socket.isClosed());
					logger.debug("是否连上socket_isConnected:" + socket.isConnected());
				}

				// 向服务器端发送数据
				out = new DataOutputStream(socket.getOutputStream());
				input = new DataInputStream(socket.getInputStream());

				logger.info("running-->"+running);
				// 消息重发
				while (running && sendTimes < REPEAT_TIMES && result == null) {
					if (sendTimes > 0) {
						// 等待睡眠，重发
						Thread.sleep(SLEEP_TIME);
					}
					NewHL7Util.sendHL7MsgByStream(message, out);
					result = NewHL7Util.getHL7MsgByStream(input);
				}

			} catch (Exception e) {
				logger.error("客户端sendHL7Message异常。", e);
				if (instance == null) {
					instance = new HL7SendThread();
					instance.start();
				}
				result = null;
			} finally {
				socketPool.releaseConnection(socket);
				try {
					if (out != null) {
						out.close();
					}
					if (input != null) {
						input.close();
					}
				} catch (IOException e) {
					logger.error("输入输出流关闭异常。", e);
				}
			}

			if (StringUtil.isNotEmpty(result)) {
				if (!"M".equals(result.substring(0, 1))) {
					logger.info("消息开头非M，补M");
					result = "M" + result;
				}
			}

			if (StringUtil.isEmpty(result)) {
				logger.info("result为空线程停止，唤起:"+result);
				logger.info("instance状态:"+instance);
				if (instance!=null) {
					logger.info("instance_isAlive运行状态:"+instance.isAlive());
					logger.info("instance_isInterrupted中断状态:"+instance.isInterrupted());
				}else {
					instance = new HL7SendThread();
					instance.start();
					instance.notifyAll();
				}
			}
			return result;
		}
	}

	private String getMessageFromSendQueue() {
		String message = null;
		synchronized (sendQueue) {
			if (sendQueue.size() <= 0) {
				try {
					sendQueue.wait();
				} catch (InterruptedException e) {
				}
			}
			message = sendQueue.poll();
			sendQueue.notifyAll();
		}
		return message;
	}

	public String sendHLMessage(String message) throws IOException {
		logger.info("send message："+message);
		String result = null;
		try {
			if (message == null) {
				return null;
			}
			String uid = NewHL7Util.getMsgControlId(message);
			try {
				String newUid = NewHL7Util.random(20);
				message = message.replace(uid, newUid);
				uid = newUid;
			} catch (NoSuchAlgorithmException e) {
				logger.error("消息ID生成失败", e);
			}
			synchronized (sendQueue) {
				if (sendQueue.size() > 100) {
					logger.debug("sending queue size > 100");
					throw new IOException("sending queue size > 100");
				}
				logger.debug("adding message to sending queue");
				sendQueue.add(message);
				sendQueue.notifyAll();
			}

			long time = System.currentTimeMillis();
			// 60秒超时
			while (running && result == null && System.currentTimeMillis() - time < 30 * 1000) {
				result = getResult(uid);
			}
		} catch (Exception e) {
			logger.error("发送消息异常", e);
		}
		return result;
	}

	private synchronized String getResult(String uid) {
		String message = null;
		synchronized (resultQueue) {
			if (resultQueue.size() <= 0) {
				try {
					resultQueue.wait(500);
				} catch (InterruptedException e) {
					return null;
				}
			}
			if (resultQueue.containsKey(uid)) {
				message = resultQueue.get(uid);
			}
		}
		return message;
	}

}
*/