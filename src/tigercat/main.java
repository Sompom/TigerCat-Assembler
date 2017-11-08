package tigercat;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class main
{
  static void printHelp(PrintStream outstream)
  {
    outstream.println("TigerCat Assembler Usage:");
    outstream.println("\tFirst Argument: Input assembly (ASCII) file");
    outstream.println("\tSecond Argument: Output machine code (binary) file");
  }

  public static void main(String[] args) throws IOException
  {
    if (args.length < 2)
    {
      printHelp(System.out);
      System.exit(0);
    }

    Path inputPath = Paths.get(args[0]);
    Path outputPath = Paths.get(args[1]);

    // Check that the input file exists
    if (!Files.exists(inputPath))
    {
      System.err.println("Input file does not exist");
      System.exit(1);
    }

    String inputString = new String(Files.readAllBytes(inputPath), Charset.defaultCharset());

    // TODO: Decide whether Assembler should be static class
    Assembler assembler = new Assembler();
    byte[] outputMC = assembler.assemble(inputString);

    Files.write(outputPath, outputMC, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
    // Write an instruction's worth of zeros to the end of the output to ensure the CPU stalls after executing the program
    byte[] zeroPadding = new byte[4];
    for (int index = 0; index < zeroPadding.length; index ++)
    {
      // Strictly not necessary, since Java initializes everything to zero,
      // but better safe than sorry!
      zeroPadding[index] = 0;
    }
    
    Files.write(outputPath, zeroPadding, StandardOpenOption.APPEND);
  }
}