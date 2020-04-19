import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
                                                               
public class DataTypeConverter {

	public static void main(String[] args) throws IOException {

		FileWriter FileWriter = new FileWriter("output.txt");
		PrintWriter writer = new PrintWriter(FileWriter);

		StringBuilder UnsignedBuilder = new StringBuilder(); // String Builder for unsigned numbers.
		StringBuilder SignedBuilder = new StringBuilder(); // String Builder for signed numbers.
		StringBuilder FloatBuilder = new StringBuilder(); // String Builder for floating point numbers.
		StringBuilder ExpBuilder = new StringBuilder(); // String Builder for exponent part of the floating point //
														// numbers.
		StringBuilder FracBuilder = new StringBuilder(); // String Builder for the binary which will be rounded at the
															// end.
		StringBuilder NormalBuilder = new StringBuilder(); // String Builder for normal numbers. Ex (1, 2, 3) etc.
		StringBuilder FloatToBinary = new StringBuilder(); // StringBuilder for floating point numbers to represent in
															// binary format.
		ArrayList<String> StringList = new ArrayList<String>(); // ArrayList for input.txt's numbers.
		System.out.println("Welcome to Data Type Converter Program.");
		StringList = getNumbers(); // Get the numbers to the StringList.
		
		 
		Scanner input = new Scanner(System.in);
		System.out.println("Little Endian --> 1");
		System.out.println("Big Endian--> 2");
		System.out.print("Please enter the byte ordering type: ");
		int ordering = input.nextInt();
		System.out.print("Please enter the floating point size (1, 2, 3, 4): ");
		int ByteSize = input.nextInt();
		double FloatNumber = 0;
        input.close();
		for (int i = 0; i < StringList.size(); i++) {

			// FLOATING POINT SECTION.
			if (StringList.get(i).contains(".")) {

				if (StringList.get(i).startsWith("-")) { // Get the negative floating point number.

					FloatNumber = Double.parseDouble(StringList.get(i).substring(1));
					
				} else {

					FloatNumber = Double.parseDouble(StringList.get(i)); // Get positive the floating point number.
				}
				
				int Bias = 0;
				int E = 0;

				// Get the Bias value from byte size.
				if (ByteSize == 1) {

					Bias = (int) (Math.pow(2, 4 - 1) - 1);
				} else if (ByteSize == 2) {

					Bias = (int) (Math.pow(2, 6 - 1) - 1);
				} else if (ByteSize == 3) {

					Bias = (int) (Math.pow(2, 8 - 1) - 1);
				} else if (ByteSize == 4) {

					Bias = (int) (Math.pow(2, 10 - 1) - 1);
				} else {

					System.out.println("Wrong type of number");
				}

				FloatBuilder = FloatToBinary(FloatNumber); // Convert the floating point number to binary number.

				if (FloatBuilder.toString().startsWith("1")) {

					if (FloatBuilder.toString().indexOf(".") == FloatBuilder.length() - 1) { // This section for the
																								// numbers without
																								// fraction part.

						// Re-order the binary value to get mantissa format.
						FloatBuilder.deleteCharAt(FloatBuilder.indexOf("."));
						FloatBuilder.insert(1, ".");

						// Get the fraction part from mantissa.
						String FractionalPart = FloatBuilder.toString().substring(FloatBuilder.indexOf(".") + 1);
						FracBuilder.setLength(0);
						FracBuilder.append(FractionalPart);

						// Get the E value from the number.
						E = FractionalPart.length();

					} else { // This section is for the numbers like 1011.00111011 (with fraction part).

						String binaryPointLength = FloatBuilder.toString().substring(1, FloatBuilder.indexOf("."));

						// Re-order the binary value to get mantissa format.
						FloatBuilder.deleteCharAt(FloatBuilder.indexOf("."));
						FloatBuilder.insert(1, ".");

						// Get the fraction part from mantissa.
						String FractionalPart = FloatBuilder.toString().substring(FloatBuilder.indexOf(".") + 1);
						FracBuilder.append(FractionalPart);

						// Get the E value from the number.
						E = binaryPointLength.length();
					}

				}
				if (FloatBuilder.toString().startsWith(".")) { // This section is for the numbers like 0.00110111
																// (smaller than 1).

					// Get the appropriate binary value.
					FloatBuilder.insert(0, "0");

					// Get the fraction part from mantissa.
					String realFrac = FloatBuilder.substring(FloatBuilder.indexOf("1"));
					FracBuilder.append(realFrac);

					// Get the E value from number
					String ECounter = FloatBuilder.substring(FloatBuilder.indexOf(".") + 1, FloatBuilder.indexOf("1"));
					E = -(ECounter.length()) - 1;

					// Re-order the binary value to get mantissa format.
					FloatBuilder.setLength(0);
					FloatBuilder.insert(0, realFrac.substring(0, 1));
					FloatBuilder.insert(1, ".");
					FloatBuilder.insert(2, realFrac.substring(1));

				}

				// Calculate exp value in type of decimal.
				int exp = Bias + E;

				if (exp > 0) { // Get the appropriate type of the exp in order to pair with byte rule.

					ExpBuilder = ExpToBinary(exp);

					if (ByteSize == 1 && ExpBuilder.length() < 4) { // 1 byte rule.

						for (int j = 0; j < 4 - ExpBuilder.length(); j++) {

							ExpBuilder.insert(j, "0");
						}
					}
					if (ByteSize == 2 && ExpBuilder.length() < 6) { // 2 byte rule.

						for (int j = 0; j < 6 - ExpBuilder.length(); j++) {

							ExpBuilder.insert(j, "0");
						}
					}
					if (ByteSize == 3 && ExpBuilder.length() < 8) { // 3 byte rule.

						for (int j = 0; j < 8 - ExpBuilder.length(); j++) {

							ExpBuilder.insert(j, "0");
						}
					}
					if (ByteSize == 4 && ExpBuilder.length() < 10) { // 4 byte rule.

						for (int j = 0; j < 10 - ExpBuilder.length(); j++) {

							ExpBuilder.insert(j, "0");
						}
					}
				}

				// Re-organize the exp value for denormalized case.

				if (exp == 0 && ByteSize == 1) {

					for (int j = 0; j < 4; j++) {

						ExpBuilder.insert(j, "0");
					}
				}
				if (exp == 0 && ByteSize == 2) {

					for (int j = 0; j < 6; j++) {

						ExpBuilder.insert(j, "0");
					}
				}
				if (exp == 0 && ByteSize == 3) {

					for (int j = 0; j < 8; j++) {

						ExpBuilder.insert(j, "0");
					}
				}
				if (exp == 0 && ByteSize == 4) {

					for (int j = 0; j < 10; j++) {

						ExpBuilder.insert(j, "0");
					}
				}
				//ROUNDING SECTION IF NECESSARRY (Handled overflow case also for the numbers like 1.1111).
				
				if (ByteSize == 1) { 

					int NumberSize = FracBuilder.length();

					if (FracBuilder.length() <= 3) {

						int index = FracBuilder.length();

						for (int j = 0; j < 3 - NumberSize; j++) {

							FracBuilder.insert(index, "0");
							index++;
						}
					} else {

						if (!FracBuilder.toString().contains("0")) {

							while (true) {

								if (FracBuilder.length() == 2) {

									break;
								}

								FracBuilder.deleteCharAt(FracBuilder.lastIndexOf("1"));
							}
							String rounded = addBinary(FracBuilder.toString(), "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
						}

						else if (FracBuilder.charAt(3) == '1') {

							String number = FracBuilder.substring(0, 3);
							String rounded = addBinary(number, "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);

						} else if (FracBuilder.charAt(3) == '0') {

							String number = FracBuilder.substring(0, 3);
							FracBuilder.setLength(0);
							FracBuilder.append(number);

						}

					}

				} else if (ByteSize == 2) {

					int NumberSize = FracBuilder.length();
					if (FracBuilder.length() <= 9) {

						int index = FracBuilder.length();

						for (int j = 0; j < 9 - NumberSize; j++) {

							FracBuilder.insert(index, "0");
							index++;
						}

					} else {

						if (!FracBuilder.toString().contains("0")) {

							while (true) {

								if (FracBuilder.length() == 8) {

									break;
								}

								FracBuilder.deleteCharAt(FracBuilder.lastIndexOf("1"));
							}
							String rounded = addBinary(FracBuilder.toString(), "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
						}

						else if (FracBuilder.charAt(9) == '1') {

							String number = FracBuilder.substring(0, 9);
							String rounded = addBinary(number, "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);

						} else if (FracBuilder.charAt(9) == '0') {

							String number = FracBuilder.substring(0, 9);
							FracBuilder.setLength(0);
							FracBuilder.append(number);

						}
					}
				} else if (ByteSize == 3) {

					int NumberSize = FracBuilder.length();

					if (FracBuilder.length() <= 13) {

						int index = FracBuilder.length();

						for (int j = 0; j < 15 - NumberSize; j++) {

							FracBuilder.insert(index, "0");
							index++;
						}
					} else {

						if (!FracBuilder.toString().contains("0")) {

							while (true) {

								if (FracBuilder.length() == 12) {

									break;
								}

								FracBuilder.deleteCharAt(FracBuilder.lastIndexOf("1"));
							}
							String rounded = addBinary(FracBuilder.toString(), "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
							FracBuilder.insert(FracBuilder.length(), "00");
						}

						else if (FracBuilder.charAt(13) == '1') {

							String number = FracBuilder.substring(0, 13);
							String rounded = addBinary(number, "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
							FracBuilder.insert(FracBuilder.length(), "00");

						} else if (FracBuilder.charAt(13) == '0') {

							String number = FracBuilder.substring(0, 13);
							FracBuilder.setLength(0);
							FracBuilder.append(number);
							FracBuilder.insert(FracBuilder.length(), "00");

						}

					}

				} else if (ByteSize == 4) {

					int NumberSize = FracBuilder.length();

					if (FracBuilder.length() <= 13) {

						int index = FracBuilder.length();

						for (int j = 0; j < 21 - NumberSize; j++) {

							FracBuilder.insert(index, "0");
							index++;
						}
					} else {

						if (!FracBuilder.toString().contains("0")) {

							while (true) {

								if (FracBuilder.length() == 12) {

									break;
								}

								FracBuilder.deleteCharAt(FracBuilder.lastIndexOf("1"));
							}
							String rounded = addBinary(FracBuilder.toString(), "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
							FracBuilder.insert(FracBuilder.length(), "00000000");
						}

						else if (FracBuilder.charAt(13) == '1') {

							String number = FracBuilder.substring(0, 13);
							String rounded = addBinary(number, "1");
							FracBuilder.setLength(0);
							FracBuilder.append(rounded);
							FracBuilder.insert(FracBuilder.length(), "00000000");

						} else if (FracBuilder.charAt(13) == '0') {

							String number = FracBuilder.substring(0, 13);
							FracBuilder.setLength(0);
							FracBuilder.append(number);
							FracBuilder.insert(FracBuilder.length(), "00000000");

						}
					}

				}
				String SignBit = "";
				if (StringList.get(i).contains("-")) {

					SignBit = "1";
				} else {

					SignBit = "0";
				}

				FloatToBinary = getBinaryRep(SignBit, ExpBuilder.toString(), FracBuilder.toString());
				
				String FloatHexValue = "";
				String orderingType = "";
				
                for (int j = 0; j < FloatToBinary.length(); j = j + 4) { // Convert the Binary number to Hexadecimal number.
                	
                	FloatHexValue += binaryToHexadecimal(FloatToBinary.toString().substring(j, j + 4));
					
				}
				if(ordering == 1) {
					
					orderingType = getLittleEndian(FloatHexValue);
					writer.print(orderingType);
					writer.print("\n");
					
				}
				else {
					
					orderingType = getBigEndian(FloatHexValue);
					writer.print(orderingType);
					writer.print("\n");

				}
				System.out.println(orderingType);

			}

			// UNSIGNED INTEGER SECTION.
			else if (StringList.get(i).contains("u")) {

				int unsignedNum = Integer.parseInt(StringList.get(i).substring(0, StringList.get(i).indexOf("u"))); // Get
																													// the
																													// number.

				UnsignedBuilder = UnsignedIntegerToBinary(unsignedNum); // Convert the given integer number to binary.
																		// // number.
				String UnsignedHexValue = "";

				for (int j = 0; j < UnsignedBuilder.length(); j = j + 4) { // Convert the binary number to hexadecimal
																			// number.

					UnsignedHexValue += binaryToHexadecimal(UnsignedBuilder.toString().substring(j, j + 4));

				}

				if (ordering == 1) {

					String UnsignedLittleEndian = getLittleEndian(UnsignedHexValue); // Get the little endian format																					// from hexadecimal number.
					System.out.println(UnsignedLittleEndian);
					writer.print(UnsignedLittleEndian);
					writer.print("\n");


				} else if (ordering == 2) {

					String UnsignedBigEndian = getBigEndian(UnsignedHexValue); // Get the big endian format from hexadecimal number.																			
					System.out.println(UnsignedBigEndian);
                    writer.print(UnsignedBigEndian);
                    writer.print("\n");

					
				} else {

					System.out.println("Wrong number type.");
				}

				// SIGNED INTEGER SECTION.
			} else if (StringList.get(i).contains("-") && !StringList.get(i).contains(".")) {

				int SignedNum = Integer.parseInt(StringList.get(i).substring(1));

				SignedNum = SignedNum - 2 * SignedNum;
				SignedIntegerToBinary(SignedNum, SignedBuilder);
				SignedBuilder.delete(0, SignedBuilder.length() / 2);
				String SignedHexValue = "";

				for (int j = 0; j < SignedBuilder.length(); j = j + 4) {

					SignedHexValue += binaryToHexadecimal(SignedBuilder.toString().substring(j, j + 4));

				}

				if (ordering == 1) {

					String SignedLittleEndian = getLittleEndian(SignedHexValue); // Get the little endian format from hexadecimal number.																					
					System.out.println(SignedLittleEndian);
					writer.print(SignedLittleEndian);
					writer.print("\n");


				} else if (ordering == 2) {

					String SignedBigEndian = getBigEndian(SignedHexValue); // Get the big endian format from hexadecimal number.																		
					System.out.println(SignedBigEndian);
					writer.print(SignedBigEndian);
					writer.print("\n");


				} else {

					System.out.println("Wrong number type.");
				}

				// OTHER POSITIVE INTEGER SECTION.
			} else {

				int NormalNum = Integer.parseInt(StringList.get(i)); // Get the given number.
				NormalBuilder = UnsignedIntegerToBinary(NormalNum); // Convert the given integer number to binary
																	// number.

				String NormalHexValue = "";

				for (int j = 0; j < NormalBuilder.length(); j = j + 4) {

					NormalHexValue += binaryToHexadecimal(NormalBuilder.toString().substring(j, j + 4));

				}

				if (ordering == 1) {

					String NormalLittleEndian = getLittleEndian(NormalHexValue); // Get the little endian format from hexadecimal number.																					
					System.out.println(NormalLittleEndian);
					writer.print(NormalLittleEndian);
					writer.print("\n");


				} else if (ordering == 2) {

					String NormalBigEndian = getBigEndian(NormalHexValue); // Get the big endian format from hexadecimal number.																	
					System.out.println(NormalBigEndian);
					writer.print(NormalBigEndian);
					writer.print("\n");

				} else {

					System.out.println("Wrong number type.");
				}
			}

		}
            writer.close();
	}

	public static StringBuilder getBinaryRep(String SignBit, String Exponent, String Fraction) { // Get the binary representation from given float number.

		StringBuilder builder = new StringBuilder();

		builder.append(SignBit);
		builder.append(Exponent);
		builder.append(Fraction);

		return builder;
	}

	public static StringBuilder ExpToBinary(int number) { // Only converts exponent part of the floating point number to
															// binary number.

		StringBuilder builder = new StringBuilder();

		while (number > 0) {

			int Remaining = number % 2;
			builder.append((((char) (Remaining + '0'))));
			number /= 2;

		}
		builder = builder.reverse();

		return builder;
	}

	public static StringBuilder FloatToBinary(double Number) { // Convert float number to binart number.

		StringBuilder builder = new StringBuilder();

		int IntegerPart = (int) Number; // Get the integer part of decimal number.
		double FractionalPart = Number - IntegerPart; // Get the fractional part decimal number.

		while (IntegerPart > 0) { // Convert integer number part to binary number.

			int Remaining = IntegerPart % 2;

			builder.append(((char) (Remaining + '0')));
			IntegerPart /= 2;
		}

		builder = builder.reverse(); // Reverse builder to get original binary number.

		builder.append("."); // Append point for fraction part.

		while (FractionalPart != 0.000) { // Convert fraction part to binary number.

			FractionalPart *= 2;
			int FractionalBit = (int) FractionalPart;

			if (FractionalBit == 1) {

				FractionalPart -= FractionalBit;
				
				builder.append((char) (1 + '0'));

			} else {

				builder.append((char) (0 + '0'));
			}

		}

		return builder;
	}

	public static String binaryToHexadecimal(String binary) { // Convert binary numbers to hexadecimal numbers..

		String result = "";
		int sum = 0;

		for (int i = binary.length(); i > 0; i--) {

			sum += Integer.parseInt(binary.substring(i - 1, i)) * Math.pow(2, binary.length() - i);
		}
		if (sum < 10) {

			result += sum;
		}

		switch (sum) {
		case 10:

			result += "A";

			break;

		case 11:

			result += "B";

			break;
		case 12:

			result += "C";

			break;
		case 13:

			result += "D";

			break;
		case 14:

			result += "E";

			break;
		case 15:

			result += "F";

			break;
		}

		return result;
	}

	public static String addBinary(String binary1, String binary2) { //Add two binary numbers.
		
		StringBuilder builder = new StringBuilder();
		
		int i = binary1.length() - 1, j = binary2.length() - 1, CarryOut = 0;
		
		while (i >= 0 || j >= 0) {
			
			int sum = CarryOut;
			
			if (j >= 0)
				sum += binary2.charAt(j--) - '0';
			
			if (i >= 0)
				sum += binary1.charAt(i--) - '0';
			
			builder.append(sum % 2);
			CarryOut = sum / 2;
		}
		if (CarryOut != 0)
			builder.append(CarryOut);
		
		return builder.reverse().toString();
	}

	public static void SignedIntegerToBinary(int number, StringBuilder builder) { // Converting signed integer to binary
																					// number. (Using 2's complement).

		if (number == 0) {

			return;
		}

		SignedIntegerToBinary((number >>> 1), builder);
		builder.append((number & 1));

	}

	public static String getLittleEndian(String hex) { // Get Little Endian format from given hexadecimal number.

		String LittleEndian = "";
		
        for (int i = hex.length(); i > 0; i = i-2) {
			
        	LittleEndian += hex.substring(i-2, i) + " " ;
		}
		
		return LittleEndian;
	}

	public static String getBigEndian(String hex) { // Get Big Endian format from given hexadecimal number.

		String BigEndian = "";
		for (int i = 0; i < hex.length(); i = i+2) {
				
	        	BigEndian += hex.substring(i, i+2) + " ";
	        	
			}
		return BigEndian;
	}

	public static StringBuilder UnsignedIntegerToBinary(int number) { // Converting unsigned integer to binary number.

		StringBuilder builder = new StringBuilder();

		while (number > 0) {

			int index = 0;
			int remaining = number % 2;
			builder.insert(index, remaining);
			number = number / 2;
			index++;
		}

		// Fill the number with all zeros (Leading zero).
		int size = builder.length();

		for (int i = 0; i < 16 - size; i++) {

			builder.insert(i, "0");
		}
		return builder;
	}

	public static ArrayList<String> getNumbers() throws IOException, FileNotFoundException {

		
		Scanner input = new Scanner(System.in);	
		ArrayList<String> List = new ArrayList<String>(); // ArrayList for input.txt's numbers.
		boolean isTrue = true;
		System.out.print("Please enter the file name: ");
		String FileName = input.next();
		while(isTrue) {
			
			try {
				
				File file = new File(FileName); // Input file.
				isTrue = false;
				BufferedReader buffer = new BufferedReader(new FileReader(file));
				// Read line by line and add the string to the arraylist.
				String LineReader = "";
				while ((LineReader = buffer.readLine()) != null) {
					List.add(LineReader);
				}
				buffer.close();
			} catch (Exception e) {
				
				System.out.print("The file not found. Please enter the file name again: ");
				FileName = input.next();
				isTrue = true;
			}
		}
		  
		return List;
	}

}
