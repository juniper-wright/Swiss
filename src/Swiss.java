import java.util.Scanner;
import java.util.Random;

public class Swiss
{
	public static void main(String args[])
	{
		Scanner scanly = new Scanner(System.in);				// your scanner
		Random randly = new Random();						// your randomizer
		int winPoints = -2000000000;
		int drawPoints = -2000000000;
		int lossPoints = -2000000000;
		int numPlayers = -1;
		while(winPoints == -2000000000 || drawPoints == -2000000000 || lossPoints == -2000000000 || numPlayers == -1)
		{
			try
			{
				if(winPoints == -2000000000)
				{
					System.out.print("How many points for a win? ");
					int win = Integer.parseInt(scanly.nextLine());
					winPoints = win;
				}
				if(drawPoints == -2000000000)
				{
					System.out.print("How many points for a draw? ");
					int draw = Integer.parseInt(scanly.nextLine());
					drawPoints = draw;
				}
				if(lossPoints == -2000000000)
				{
					System.out.print("How many points for a loss? ");
					int loss = Integer.parseInt(scanly.nextLine());
					lossPoints = loss;
				}
				if(numPlayers == -1)
				{
					System.out.print("How many players? ");
					int npl = Integer.parseInt(scanly.nextLine());
					if(npl < 2)
						throw new Exception();
					numPlayers = npl;
				}
			}
			catch(Exception e)
			{
				System.out.println("Invalid input.");
			}
		}
		int rounds = (int) Math.ceil((Math.log(numPlayers)/Math.log(2)));	// Figure out the number of rounds
		System.out.println("There will be " + rounds + " rounds.");	// This is equal to ceil(log2(numPlayers))
		int[] score = new int[numPlayers];			// contains the players' score
		int[][] matchups = new int[numPlayers/2][2];		// contains the player matchups
		boolean[] matched = new boolean[numPlayers];		// Determines whether the player has been matched yet
		boolean[] byed = new boolean[numPlayers];		// Indicates whether the player has been byed yet
		int[][] alreadyPlayed = new int[numPlayers][rounds];	// Keeps track of which players each player has
									// played, so as to avoid duplication
		String[] names = new String[numPlayers];		// Contains the names of the players, for UI friendliness

		for(int i = 0;i < numPlayers;i++)
		{
			for(int j = 0;j < rounds;j++)		// Blank out the alreadyPlayed array
			{
				alreadyPlayed[i][j] = -1;
			}
			System.out.print("Enter the name of the " + (i+1) + ordinalEndings(i+1) + " player: ");
			names[i] = scanly.nextLine();		// Fill in the players' names
			score[i] = 0;				// Set their scores to 0
		}

		for(int currRound = 0;currRound < rounds;currRound++)
		{
			System.out.println("======================");
			System.out.println("==     ROUND " + (currRound+1) + "      ==");
			System.out.println("======================");
			int bye = -1;
			if(numPlayers%2 == 1)		// If odd players,
			{
				bye = 0;		// If it's not the first round, give the bye to the person
							// with the lowest score.
				if(currRound == 0)	// If it's the first round, give the bye randomly
				{
					bye = randly.nextInt(numPlayers);
				}
				while(byed[bye] == true)			// and make sure they haven't had a bye yet
				{
					bye++;
					if(bye == byed.length)		// This is the condition that you get to the end of
					{				// the list of players without finding anyone.
						bye--;			// This is unlikely, but in this case, the bye
						while(byed[bye] == true)// is chosen completely at random.
						{
							bye = randly.nextInt(numPlayers);
						}
					}
				}

				score[bye] += winPoints;	// they get a bye, meaning they automatically win
				System.out.println(names[bye] + " has been given a bye, and automatically wins this round. Congratulations.");
				byed[bye] = true;
				matched[bye] = true;
			}
			// MAIN ROUND MANAGEMENT BLOCK
			int matchcount = 0;
			boolean impossible = false;
			int randomize = 0;
			for(int i = 0;i < numPlayers;i++)		// PLAYER PAIRING LOOP
			{						// For each player...
				impossible = false;
				if(bye == i || matched[i] == true)	// If this player has been byed, or has
				{					// already been matched, then nothing needs
					continue;			// to be done. Continue.
				}
				else
				{
					int match = i+1;
					int count = 0;
					if(currRound == 0)
					{
						match = randly.nextInt(numPlayers);
					}
					while((existsIn(match, alreadyPlayed[i]) && randomize < 10) || matched[match] == true || i == match || match == numPlayers)
					{
						count++;
						if((count > numPlayers*10 && randomize > 0) || count > numPlayers)
						{
							impossible = true;

							if(randomize == 0)
							{
								System.out.println("ERROR: Failed to match players based on their score. This round is fully randomized.");
							}
							randomize++;
							break;
						}
						if(randomize > 0)
						{
							match = randly.nextInt(numPlayers);
						}
						else
						{
							match++;
						}
						if(match == matched.length)
						{
							match = randly.nextInt(numPlayers);
						}
					}
					if(impossible == false)
					{
						matchups[matchcount][0] = i;
						matchups[matchcount][1] = match;
						matched[i] = true;
						matched[match] = true;
						alreadyPlayed[i][currRound] = match;
						alreadyPlayed[match][currRound] = i;
						matchcount++;
					}
					else
					{
						for(int j = 0;j < numPlayers;j++)
						{
							matched[j] = false;
							alreadyPlayed[j][currRound] = -1;
						}
						if(bye >= 0)
							matched[bye] = true;
						i = 0;
						matchcount = 0;
					}
				}
			}
			if(bye >= 0)
				matched[bye] = false;
			// Active user-input game management loop
			while(gameLeft(matched))
			{
				System.out.println("Games this round:");
				for(int i = 0;i < matchups.length;i++)
				{
					System.out.print((i+1) + ".) " + names[matchups[i][0]] + " vs. " + names[matchups[i][1]]);

					if(matched[matchups[i][0]] == false && matched[matchups[i][1]] == false)
					{
						System.out.print(" (Finished)");
					}

					System.out.println();
				}
				System.out.print("Please input the number of the game you would like to report on: ");
				int game = -1;
				while(game == -1)
				{
					try
					{
						game = Integer.parseInt(scanly.nextLine()) - 1;
						if(game > matchups.length || game < 0 || matched[matchups[game][0]] == false)
						{
							throw new Exception();
						}
					}
					catch(Exception e)
					{
						if(game >= 0 && game <= matchups.length && matched[matchups[game][0]] == false)
							System.out.println("That game has already been reported.");
						else
							System.out.println("That is not a valid game.");
						game = -1;
						System.out.print("Please input the number of the game you would like to report on: ");
					}
				}
				System.out.print("For " + names[matchups[game][0]] + ", please input the result (W/L/D): ");
				String wld = scanly.nextLine();
				wld = wld.toLowerCase();
				if(wld.equals("w"))
				{
					score[matchups[game][0]] += winPoints;
					score[matchups[game][1]] += lossPoints;
					matched[matchups[game][0]] = false;
					matched[matchups[game][1]] = false;
				}
				else if(wld.equals("l"))
				{
					score[matchups[game][0]] += lossPoints;
					score[matchups[game][1]] += winPoints;
					matched[matchups[game][0]] = false;
					matched[matchups[game][1]] = false;
				}
				else if(wld.equals("d"))
				{
					score[matchups[game][0]] += drawPoints;
					score[matchups[game][1]] += drawPoints;
					matched[matchups[game][0]] = false;
					matched[matchups[game][1]] = false;
				}
				else
				{
					System.out.println("You have entered an invalid choice.");
				}
			}
			// End active user-input game management loop

			for(int i = 0;i < numPlayers;i++)
			{
				matched[i] = false;
			}

			// EACH ROUND, SORT ALL PLAYERS.
			// You're sorting on score in nondecreasing order
			String sswap;
			int iswap;
			boolean bswap;
			for(int i = 0;i < numPlayers;i++)
			{
				for(int j = 0;j < numPlayers-1;j++)
				{
					if(score[j+1] < score[j])
					{
						sswap = names[j];
						names[j] = names[j+1];
						names[j+1] = sswap;
						iswap = score[j];
						score[j] = score[j+1];
						score[j+1] = iswap;
						bswap = matched[j];
						matched[j] = matched[j+1];
						matched[j+1] = bswap;
						bswap = byed[j];
						byed[j] = byed[j+1];
						byed[j+1] = bswap;
						for(int k = 0;k < rounds;k++)
						{
							iswap = alreadyPlayed[j][k];
							alreadyPlayed[j][k] = alreadyPlayed[j+1][k];
							alreadyPlayed[j+1][k] = alreadyPlayed[j][k];
						}
					}
				}
			}
			// END MAIN ROUND MANAGEMENT BLOCK
			// Output everyone's score for review
			for(int i = 0;i < numPlayers;i++)
			{
				System.out.print(names[i] + " has scored " + score[i] + " point");
				if(score[i] != 1)
					System.out.print("s");
				System.out.println(".");
			}
		}
		// Determine winners, etc. Does not tie-break at all
		for(int i = 0;i < numPlayers;i++)
		{
			System.out.println("====================================");
			System.out.println("     " + (numPlayers-i) + ordinalEndings(numPlayers-i) + " Place:");
			System.out.println("     " + names[i] + " - " + score[i] + "pts");
		}
		System.out.println("====================================");

		scanly.close();
	}

	// Determine whether or not there are any unresolved games
	public static boolean gameLeft(boolean[] inArr)
	{
		for(int i = 0;i < inArr.length;i++)
		{
			if(inArr[i] == true)
				return true;
		}
		return false;
	}

	// Determine ordinal endings
	public static String ordinalEndings(int i)
	{
		if(i%10==1 && i%100!=11)
			return "st";
		else if(i%10==2 && i%100!=12)
			return "nd";
		else if(i%10==3 && i%100!=13)
			return "rd";
		else
			return "th";
	}

	// Simple integer bubblesort
	public static int[] bubblesort(int[] inArr)
	{
		for(int i = 0;i < inArr.length;i++)
		{
			for(int j = 0;j < inArr.length-1;j++)
			{
				if(inArr[j+1] > inArr[j])
				{
					int temp = inArr[j];
					inArr[j] = inArr[j+1];
					inArr[j+1] = temp;
				}
			}
		}
		return inArr;
	}

	// Determine whether an array of integers contains another integer
	public static boolean existsIn(int needle, int[] haystack)
	{
		for(int i = 0;i < haystack.length;i++)
		{
			if(haystack[i] == needle)
				return true;
		}
		return false;
	}

	// Find the index of the arithmetically-least element in the integer array
	public static int findLowestIndex(int[] inArr)
	{
		int lowest = Integer.MAX_VALUE;
		int lowestIndex = Integer.MAX_VALUE;
		for(int i = 0;i < inArr.length;i++)
		{
			if(inArr[i] < lowest)
			{
				lowest = inArr[i];
				lowestIndex = i;
			}
		}
		return lowestIndex;
	}
}
