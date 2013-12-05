import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * kedze vrcholy boli zadane od 1 ja som si to precislo od NULY ale vypisujem od jednotky
 * cize v mape su ulozene od nuly 
 * napr. ak je pUC 9 tak uz mna to je 8, ale vypisujem 9,
 * **/

public class Republic{
	private boolean[][] cityMap;
	private int numberOfCities;
	private int numberOfRoads;
	private Integer pucCity;
	
	public Republic(String fileName) throws FileNotFoundException
	{
		numberOfCities = 0;
		numberOfRoads = 0;
		try
		{
			String temp;
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			temp  = reader.readLine();
			try{
				numberOfCities = Integer.parseInt(temp);
				cityMap = new boolean[numberOfCities][numberOfCities];
				buildCityMap(reader);
			}catch(Exception e)
			{
				System.out.println(e.toString());
			}
			finally
			{
				reader.close();
			}
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
	}
	
	private void buildCityMap(BufferedReader reader) throws IOException
	{
		String temp;
		String[] nums;
		while((temp=reader.readLine())!=null)
		{
			try{
				nums = temp.split(" ");
				if(Integer.parseInt(nums[0])==0)
				{
					pucCity = Integer.parseInt(nums[1])-1;
					return;
				}
				makeNeighbours(Integer.parseInt(nums[0])-1, Integer.parseInt(nums[1])-1);
				
			}catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
	}
	
	private void makeNeighbours(Integer city1, Integer city2)
	{
		try
		{
			numberOfRoads++;
			cityMap[city1][city2] = true;
			cityMap[city2][city1] = true;
			
		}catch(ArrayIndexOutOfBoundsException e)
		{
			throw e;
		}
	}
	
	private int numberOfRoadsToCity(int cityName)
	{
		int res=0;
		for(int i=0;i<cityMap[cityName].length;i++)
			if(cityMap[cityName][i]) res++;
		return res;
	}
	
	private String getAllRoadToCity(int cityName)
	{
		String res="{";
		for(int i=0;i < cityMap[cityName].length;i++)
		{
			if(cityMap[cityName][i])
			{
				res+="{";
				res+=Integer.toString(cityName+1)+","+Integer.toString(i+1);
				res+="} ";
			}
		}
		return res+"}";
	}
	
	private ArrayList<Integer> getCitiesWithTheMostCountOfRoads()
	{
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int max=0;
		for(int i=0;i<numberOfCities;i++)
		{
			if(numberOfRoadsToCity(i)>=numberOfRoadsToCity(max))
				max = i;
		}
		temp.add(max);
		for(int i=0;i<numberOfCities;i++)
			if(!temp.contains(i) && numberOfRoadsToCity(max)==numberOfRoadsToCity(i))
				temp.add(i);
		return temp;
	}
	
	private String getCitiesWithTheMostCountOfRoadsString()
	{
		String temp=" | ";
		for(Integer i:getCitiesWithTheMostCountOfRoads())
		{
			temp+="Mesto:"+ Integer.toString(i+1)+", cesty: "+getAllRoadToCity(i);
			temp+=" | ";
		}
		return temp;
	}
	
	//odpojim mesto PucCIty z republiky 
	private boolean[][] getCityMapWithoutPucCity(Integer pucCity)
	{
		boolean ret[][] = new boolean[numberOfCities][numberOfCities];
		
		for(int i=0;i < numberOfCities;i++)
			for(int j=0;j<numberOfCities;j++)
			{
				if(i==pucCity || j==pucCity)
					ret[i][j] = false;
				else
					ret[i][j] = cityMap[i][j];
			}

		return ret;
	}
	
	private Integer getFirstElementFromSet(HashSet<Integer> set)
	{
		if (set.size()<1) return -1;
		return (Integer)set.toArray()[0];
	}
	
	public HashMap<Integer,HashSet<Integer>> getRepublicComponents(boolean[][] map,Integer pucCity) //nahdaze mi do mapy komonenty grafu na ake sa rozpadne republika po PUC pricom PUC mesto vynecha
	{
		HashMap<Integer,HashSet<Integer>> temp = new  HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> allCities = new HashSet<Integer>();// cisla miest bez pucu
		HashSet<Integer> oneComponent = new HashSet<Integer>(); //jeden komponent
		for(int i=0;i<map.length;i++)
		{
			allCities.add(i);
		}
		allCities.remove(pucCity);
		int count=0;
		oneComponent = breadthFirstSearch(map, getFirstElementFromSet(allCities));
		temp.put(count, oneComponent);
		count++;
		while(allCities.size() > 1)
		{
		
			allCities.removeAll(oneComponent);
			if(allCities.size()==0) break;
			oneComponent = breadthFirstSearch(map, getFirstElementFromSet(allCities));
			temp.put(count, oneComponent);
			count++;
		}
		
		return temp;
	}
	
	private String fixConnectivityBetweenCities(boolean[][] map,Integer pucCity) //vypise ako opravit znefunkcennu siet ak sa da ak nie tak vypise ze sa neda
	{
		String ret="";
		HashMap<Integer,HashSet<Integer>> components = this.getRepublicComponents(map, pucCity);
		int pocKomp = components.size();
		ret+="Po Puci v meste c.:"+Integer.toString(pucCity)+" sa nam republika rozpadla na: "+Integer.toString(pocKomp)+" komponenty";
		ret+="\r\n";
		System.out.println("komponenty"+components.toString());
		if(pocKomp <2)
		{
			ret+="mesto sa nam aj Puci nerozpadlo vsetko je v poriadku mozme cestovat vsade :)";
			ret+="\r\n";
		}
		else if(pocKomp <3)
		{
			
			int m1,m2;
			m1 = getFirstElementFromSet(components.get(0));
			m2 = getFirstElementFromSet(components.get(1));
			ret+="mesto sa nam po Puci rozpadlo na 2 komponenty situaciu napravime vybudovanim napr. medzi mestom c.: ("+Integer.toString(m1)+" a "+Integer.toString(m2)+")";
			ret+="\r\n";
		}
		else
		{
			ret+="mesto sa nam po Puci rozpadlo na viac ako 3 komponenty jedna nova vybudovana cesta nam uz nepomoze :(";
			ret+="\r\n";
		}
		ret+="\r\n";
		
		return ret;
	}
	
	private boolean isOneRoadCity()
	{
		int poc=0;
		for(int i=0;i < numberOfCities;i++)
		{
			for(int j=0;j < numberOfCities;j++)
			{
				if(cityMap[i][j]) poc++;
			}
			if(poc==1) 
				return true;
			else 
				poc=0;
		}
		return false;
	}
	
	public boolean isConnected(boolean[][] map)
	{
		HashSet<Integer> visited = new HashSet<Integer>();
		ArrayList<Integer> path = new ArrayList<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		int start=0;
		queue.offer(start);
		int v;
		while(!queue.isEmpty())
		{
			v = queue.poll();
			
			if(!path.contains(v))path.add(v);
			
			visited.add(v);
			for(int i=0;i<numberOfCities;i++)
			{
				if(map[v][i] &&  !visited.contains(i))
					queue.offer(i);
			}
		}
		//System.out.println("cesta: "+path.toString());
		return (queue.isEmpty() && isCorrectVisited(visited,this.pucCity));
	}
	
	public HashSet<Integer> breadthFirstSearch(boolean[][] map,Integer start) //prehladavanie do sirky na vstupnom grafe v start vrchole zacinam vrati mi cestu
	{
		HashSet<Integer> visited = new HashSet<Integer>();
		HashSet<Integer> path = new HashSet<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.offer(start);
		int v;
		while(!queue.isEmpty())
		{
			v = queue.poll();
			path.add(v);
			visited.add(v);
			
			for(int i=0;i<map.length;i++)
			{
				if(map[v][i] &&  !visited.contains(i))
					queue.offer(i);
			}
		}
		return path;
	}
	
	private void connectRepublicComponents()
	{
		
	}
	
	//overim mnozinu visited mnozina musi neobsahovat prave PUC mesto
	private boolean isCorrectVisited(HashSet<Integer> visited, Integer pucCity)
	{
		if(visited.size() != numberOfCities-1) return false;
		int i=0;
		for(Integer v:visited)
		{
			if(i==pucCity) i++;
			if(!visited.contains(i)) return false;
			i++;
		}
		return true;
	}
	
	
	public String toString()
	{
		String out="";
		out+= "***Prva poduloha***\r\n";
		out+= "Cislo mesta Puc: "+Integer.toString(this.pucCity+1)+"\r\n";
		out+= "Pocet miest v republike: "+Integer.toString(numberOfCities)+"\r\n";
		out+= "Pocet priamych ciest v republike: "+Integer.toString(numberOfRoads)+"\r\n";
		out+= "Pocet ciest do mesta Puc: "+Integer.toString(numberOfRoadsToCity(pucCity))+"\r\n";
		out+= "Existuje mesto z ktoreho vedie iba jedna cesta: "+Boolean.toString(isOneRoadCity())+"\r\n";
		out+= "Mesto(a) s najvacsim poctom ciest: "+getCitiesWithTheMostCountOfRoadsString()+"\r\n";
		out+= "***Prva poduloha***\r\n";
		out+= "***Druha poduloha***\r\n";
		out+= "Je mozne cestovat do kazdeho mesta v republike aj po Puci(okrem Puc mesta): "+isConnected(getCityMapWithoutPucCity(this.pucCity))+"\r\n";
		out+= "***Druha poduloha***\r\n";
		out+= "***Tretia poduloha***\r\n";
		out+= fixConnectivityBetweenCities(getCityMapWithoutPucCity(this.pucCity),this.pucCity)+"\r\n";
		out+= "***Tretia poduloha***\r\n";
		out+= "\r\n";
		out+= "Mapa republiky:\r\n";
		out+="   ";
		for(int i = 0;i<cityMap.length;i++)
		{
			out+=Integer.toString(i+1);
			out+=(i==cityMap.length-2)?" ":"  ";
		}
		out+="\r\n";
		for(int i = 0;i<cityMap.length;i++)
		{
			out+=Integer.toString(i+1);
			out+=(i+1>9)?" ":"  ";
			for(int j=0;j<cityMap.length;j++)
			{	
				int res = (cityMap[i][j])?1:0;
				out+=Integer.toString(res)+"  ";
				
			}
			out+="\r\n";
		}
		System.out.println();
		return out;
	}
	
	public void writeToFile(String fileName) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		try {
			writer.write(this.toString());
		} catch (Exception e) {
			System.out.println(e.toString());
		}finally
		{
			writer.close();
		}
	}
}
