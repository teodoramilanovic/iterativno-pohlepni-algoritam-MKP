package ig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IterativnoPohlepni {
	private static int brojDimenzija;
	private static int brojPredmeta;
	private static float[][] tezine;
	private static float[] profiti;
	private static float[] kapaciteti;
	private static int fd;
	private static int bi;
	private static List<Map.Entry<Integer,Float>> fitnesi;
	
	public static void procitajDatoteku(String datoteka, int faktorDestrukcije, int brojIteracija) throws Exception {
		fd=faktorDestrukcije;
		bi=brojIteracija;
		
		File dat = new File(datoteka);
	    BufferedReader br = new BufferedReader(new FileReader(dat));
	    
	    File infoDat=new File("instanceRezultati.txt");
	    String ime="ITERATIVNO POHLEPNI FITNES "+datoteka +" fd="+fd+" bi="+bi;
	    Files.write(infoDat.toPath(), ime.getBytes(), StandardOpenOption.APPEND);
	    String noviRed="\n";
	    Files.write(infoDat.toPath(), noviRed.getBytes(), StandardOpenOption.APPEND);
	    
	    String red=br.readLine();
	    String brojInstanci=red.split(" ")[1];
	    int broj=Integer.parseInt(brojInstanci);
	    
	    for(int brojac=0; brojac<broj; brojac++) {
	 
	    	red=br.readLine();
		    brojDimenzija=Integer.parseInt(red.split(" ")[2]);
		    brojPredmeta=Integer.parseInt(red.split(" ")[1]);
		    tezine=new float[brojPredmeta][brojDimenzija];
		    profiti=new float[brojPredmeta];
		    kapaciteti=new float[brojDimenzija];
		    
		    int granica1=brojPredmeta/7+1;
		    
		    int brojacP=0;
		    for(int k=0; k<granica1; k++) {
	    		red=br.readLine();
	    		String[]profitiPredmeta=red.split(" ");
	    		for(int j=0; j<profitiPredmeta.length-1; j++) {
	    			profiti[brojacP++]=Float.parseFloat(profitiPredmeta[j+1]);
		    	}
	    		
	    	}
		    
		    for(int i=0; i<brojDimenzija; i++) {
		    	int brojacD=0;
		    	for(int k=0; k<granica1; k++) {
		    		red=br.readLine();
			    	String[]tezinePredmeta=red.split(" ");
			    	for(int j=0; j<tezinePredmeta.length-1; j++) {
			    		tezine[brojacD++][i]=Float.parseFloat(tezinePredmeta[j+1]);
			    	}
		    	}
		    }
		    
		    int granica2=brojDimenzija/7+1;
		    
		    int brojacK=0;
		    for(int k=0; k<granica2; k++) {
	    		red=br.readLine();
	    		String[]ruksaci=red.split(" ");
	    		for(int j=0; j<ruksaci.length-1; j++) {
	    			kapaciteti[brojacK++]=Float.parseFloat(ruksaci[j+1]);
		    	}
	    	}
		    
		    final long pocetnoVrijeme = System.currentTimeMillis();
	    	float profit=iterativnoPohlepniAlgoritam();
	    	final long zavrsnoVrijeme = System.currentTimeMillis();
	    	String info="Instanca "+(brojac+1)+" Profit: "+profit+" Vrijeme izvrsavanja: "+(zavrsnoVrijeme-pocetnoVrijeme)+"\n";
	    	
	    	Files.write(infoDat.toPath(), info.getBytes(), StandardOpenOption.APPEND);
	    }
	    
	    Files.write(infoDat.toPath(), noviRed.getBytes(), StandardOpenOption.APPEND);
	}
	
	public static float izracunajProfit(int[]ubacen) {
		
		float ukupanProfit=0;
		for(int i=0; i<brojPredmeta; i++) {
			if(ubacen[i]==1)
				ukupanProfit+=profiti[i];
		}
		
		return ukupanProfit;
	}
	
	public static HashMap<Integer,Float> izracunajFitnese(int[]rjesenje) {
		
		ArrayList<Integer>indeksi=new ArrayList<>();
		
		for(int i=0; i<brojPredmeta; i++)
			if(rjesenje[i]==0)
				indeksi.add(i);
		
		HashMap<Integer,Float> lista=new HashMap<>();
		for(int i=0; i<indeksi.size(); i++) {
			
			float tezina=0;
			for(int j=0; j<brojDimenzija; j++) {
				tezina+=tezine[indeksi.get(i)][j];
			}
			float srednjaTezina=tezina/brojDimenzija;
			float razmjera=profiti[indeksi.get(i)]/srednjaTezina;
			
			lista.put(indeksi.get(i),razmjera);
		}
		
		
		return lista;
	}
	
	public static int[] inicijalizacija() {
		int[]rjesenje=new int[brojPredmeta];
		
		HashMap<Integer,Float> lista=izracunajFitnese(rjesenje);
		fitnesi = new LinkedList<Map.Entry<Integer,Float>>(lista.entrySet());
		Collections.sort(fitnesi, Collections.reverseOrder(new Comparator<Map.Entry<Integer, Float>>() {
            public int compare(Map.Entry<Integer, Float> r1, Map.Entry<Integer, Float> r2)
            {
                return (r1.getValue()).compareTo(r2.getValue());
            }
        }));

		
		for (Map.Entry<Integer,Float> predmet : fitnesi) {
			rjesenje[predmet.getKey()]=1;
			
			if(!zadovoljavaOGR(rjesenje)) {
				rjesenje[predmet.getKey()]=0;
			}
		}

		return rjesenje;
		
	}
	
	public static int dodajNoviPredmet(int[]rjesenje) {
		
		float profit=izracunajProfit(rjesenje);
		ArrayList<Integer>indeksi=new ArrayList<>();
		
		for(int i=0; i<brojPredmeta; i++)
			if(rjesenje[i]==0)
				indeksi.add(i);
		
		int najbolji=-1;
		for(int i=0; i<indeksi.size(); i++) {
			
			rjesenje[indeksi.get(i)]=1;
			
			if(!zadovoljavaOGR(rjesenje)) {
				rjesenje[indeksi.get(i)]=0;
				continue;
			}
			
			float noviProfit=izracunajProfit(rjesenje);
			
			if(noviProfit>profit) {
				profit=noviProfit;
				najbolji=indeksi.get(i);
			}
			
			rjesenje[indeksi.get(i)]=0;
		}
		
		return najbolji;
	}
	
	public static int[] lokalnaPretraga(int[]rjesenjeA) {
		
		ArrayList<Integer>indeksi=new ArrayList<>();
		Random rand=new Random();
		
		for(int i=0; i<brojPredmeta; i++)
			if(rjesenjeA[i]==1)
				indeksi.add(i);
		
		boolean improve=true;
		float maksProfit=izracunajProfit(rjesenjeA);
		
		while(improve) {
			improve=false;
			int[]rjesenjeB=rjesenjeA.clone();
			
			for(int i=0; i<indeksi.size()*2; i++) {
				int indeks=rand.nextInt(indeksi.size());
				rjesenjeB[indeksi.get(indeks)]=0;
				
				
				int najbolji=dodajNoviPredmet(rjesenjeB);
				rjesenjeB[najbolji]=1;
				float profit=izracunajProfit(rjesenjeB);
				
				if(profit>maksProfit) {
					
					maksProfit=profit;
					rjesenjeA=rjesenjeB.clone();
					
					indeksi.clear();
					for(int j=0; j<brojPredmeta; j++)
						if(rjesenjeA[j]==1)
							indeksi.add(j);
					
					improve=true;
				}
				else {
					rjesenjeB=rjesenjeA.clone();
				}
			}
		}
		
		return rjesenjeA;
	}
	
	public static float ukupnaTezina(int[]rjesenje, int indeks) {
		
		float suma=0;
		for(int j=0; j<brojPredmeta; j++) {
			if(rjesenje[j]==1) {
				suma+=tezine[j][indeks];
			}
		}
		return suma;
	}
	
	public static int[] konstrukcija(int[]rjesenje) {
		
		HashMap<Integer,Float>predmeti=new HashMap<Integer,Float>();
		
		for(Map.Entry<Integer,Float> f: fitnesi) {
			if(rjesenje[f.getKey()]==0)
				predmeti.put(f.getKey(),f.getValue());
		}
		
		for (Map.Entry<Integer,Float> predmet : predmeti.entrySet()) {
			rjesenje[predmet.getKey()]=1;
			
			if(!zadovoljavaOGR(rjesenje)) {
				rjesenje[predmet.getKey()]=0;
			}
		}

		return rjesenje;
	}
	
	public static int[] destrukcija(int[]rjesenje) {
		
		ArrayList<Integer>indeksi=new ArrayList<>();
		Random rand=new Random();
		
		for(int i=0; i<brojPredmeta; i++)
			if(rjesenje[i]==1)
				indeksi.add(i);
		
		for(int i=0; i<fd; i++) {
			int indeks=rand.nextInt(indeksi.size());
			rjesenje[indeksi.get(indeks)]=0;
		}
			
		return rjesenje;
	}
	
	
	public static boolean zadovoljavaOGR(int[]rjesenje) {
		
		
		for(int i=0;i<brojDimenzija;i++) {
			float suma=0;
			for(int j=0; j<brojPredmeta; j++) {
				if(rjesenje[j]==1) {
					suma+=tezine[j][i];
				}
			}
			if(suma>kapaciteti[i])
				return false;
		}
		return true;
	}
	
	public static float iterativnoPohlepniAlgoritam() {
		
		int[]rjesenjeA=inicijalizacija();
		
		rjesenjeA=lokalnaPretraga(rjesenjeA);
		float maksProfit=izracunajProfit(rjesenjeA);
		
		for(int i=0; i<bi; i++) {
			int[]rjesenjeB=rjesenjeA.clone();
			
			rjesenjeB=destrukcija(rjesenjeB);
			rjesenjeB=konstrukcija(rjesenjeB);
			rjesenjeB=lokalnaPretraga(rjesenjeB);
			
			float profit=izracunajProfit(rjesenjeB);
			if(profit>maksProfit) {
				maksProfit=profit;
				rjesenjeA=rjesenjeB.clone();
			}
			
		}
		System.out.println(zadovoljavaOGR(rjesenjeA));
		return izracunajProfit(rjesenjeA);
		
	}
	public static void main(String[]args) throws Exception {
		procitajDatoteku("mknapcb1", 4, 5000);
	}
}

