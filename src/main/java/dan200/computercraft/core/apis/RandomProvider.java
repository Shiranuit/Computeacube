package dan200.computercraft.core.apis;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class RandomProvider
{
  private SecureRandom random;
  
  public static SecureRandom create()
  {
    try
    {
      return SecureRandom.getInstance("SHA1PRNG");
    }
    catch (NoSuchAlgorithmException e) {}
    return new SecureRandom();
  }
  
  public SecureRandom get()
  {
    SecureRandom random = this.random;
    if (random == null)
    {
      try
      {
        random = SecureRandom.getInstance("SHA1PRNG");
      }
      catch (NoSuchAlgorithmException e)
      {
        random = new SecureRandom();
      }
      this.random = random;
    }
    return random;
  }
  
  public void seed(BigInteger seed)
  {
    get().setSeed(seed.toByteArray());
  }
  
  public void seed()
  {
    SecureRandom random = get();
    random.setSeed(random.generateSeed(4));
    this.random = random;
  }
}
