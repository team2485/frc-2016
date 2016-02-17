package org.usfirst.frc.team2485.util;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author Jeremy McCulloch
 * @author Nicholas Contreras
 */
public class LidarWrapper extends SensorBase {

	private I2C m_i2c;

	public LidarWrapper(Port port) {
		m_i2c = new I2C(port, 0x62);
	}

	public int getDistance() {

		byte[] buffer;
		buffer = new byte[2];

		buffer[0] = 0x00;
		buffer[1] = 0x00;

		m_i2c.write(0x00, 0x04);

		read(0x8f, buffer, 2);

		return (int) (Integer.toUnsignedLong(buffer[0] << 8) + Byte.toUnsignedInt(buffer[1]));
	}

	private void read(int register, byte[] buffer, int count) {
		int busyFlag = 0;
		int busyCounter = 0;

		while (busyFlag != 0) {
			byte[] testSignal = { 0x1 };
			boolean nack = m_i2c.writeBulk(testSignal);

			if (nack) {
				throw new BadLidarDataException("WriteBulk failed to write (in bulk): " + testSignal);
			}

			byte testBuffer[] = new byte[1];
			m_i2c.readOnly(testBuffer, 1);
			busyFlag = testBuffer[0];

			busyCounter++;
			if (busyCounter > 9999) {
				throw new BadLidarDataException("Lidar was too busy: " + busyFlag);
			}
		}

		if (busyFlag == 0) {
			byte[] registerSignal = { (byte) register };
			boolean nack = m_i2c.writeBulk(registerSignal);
			if (nack) {
				throw new BadLidarDataException("Unable to write (bulk) register signal: " + registerSignal);
			}
			m_i2c.readOnly(buffer, count);
		}

		if (busyCounter > 9999) {
			throw new BadLidarDataException("Lidar was too busy: " + busyFlag);
		}

	}

	@SuppressWarnings("serial")
	class BadLidarDataException extends RuntimeException {

		public BadLidarDataException(String message) {
			super(message);
		}
	}
}
