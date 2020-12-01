package com.univocity.freecommerce.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.google.zxing.qrcode.decoder.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class QrCode {

	public static BufferedImage generateQRCodeImage(String content, int width, int height) throws WriterException {
		QRCodeWriter barcodeWriter = new QRCodeWriter();

		Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		hints.put(EncodeHintType.MARGIN, 0);

		BitMatrix bitMatrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}

	public static byte[] generateQRCodeImageBytes(String content, int width, int height) {
		try {
			BufferedImage image = generateQRCodeImage(content, width, height);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			byte[] imageBytes = baos.toByteArray();
			baos.close();
			return imageBytes;
		} catch (Exception e) {
			return null;
		}
	}
}
