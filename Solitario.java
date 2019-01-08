import java.util.*;

public class Solitario {
	
	private LRUCache inviables = new LRUCache(4194304);
	private LRUCache tableros = new LRUCache(4194304);
	private double tested = 0;
	private double hits = 0;
	private long startTime = 0;
    private Pagoda pag = new Pagoda();
	
	public class Movimiento {
		public int desde_x;
		public int desde_y;
		public int quito_x;
		public int quito_y;
		public int hasta_x;
		public int hasta_y;
		
		public String toString() {
			StringBuffer output = new StringBuffer();
			//output.append("[").append(desde_x).append(",").append(desde_y).append("],[")
			//.append(quito_x).append(",").append(quito_y).append("]->[")
			//.append(hasta_x).append(",").append(hasta_y).append("]");
			output.append("[").append(desde_x).append(",").append(desde_y).append("]->[")
			.append(hasta_x).append(",").append(hasta_y).append("]");
			return output.toString();
		}
		
		public Movimiento(int d_x, int d_y, int q_x, int q_y, int h_x, int h_y) {
			desde_x = d_x;
			desde_y = d_y;
			quito_x = q_x;
			quito_y = q_y;
			hasta_x = h_x;
			hasta_y = h_y;
		}
	}
	
	public class Pagoda {
		public int valores[][] = {
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			{0,-1,1,0,1,0,1,-1,0},
			{0,0,0,0,0,0,0,0,0},
			{0,-1,1,0,1,0,1,-1,0},
			{0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0},
			};
    }

	public class Tablero {
		public int bolas[][];

		public void init() {
			int a[][] = {
			{2,2,2,2,2,2,2,2,2},
			{2,2,2,1,1,1,2,2,2},
			{2,2,2,1,1,1,2,2,2},
			{2,1,1,1,1,1,1,1,2},
			{2,1,1,1,0,1,1,1,2},
			{2,1,1,1,1,1,1,1,2},
			{2,2,2,1,1,1,2,2,2},
			{2,2,2,1,1,1,2,2,2},
			{2,2,2,2,2,2,2,2,2},
			};
			bolas = a;
		}
		
		public String toString() {
			StringBuffer output = new StringBuffer();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
                    if (bolas[i][j] == 0) {
    					output.append(" ");
                    } else if (bolas[i][j] == 1) {
    					output.append("O");
                    } else if (bolas[i][j] == 2) {
    					output.append(".");
                    }
				}
				output.append("\n");
			}
			return output.toString();
		}

		public int hashCode() {
			int res = 0;
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if ((bolas[i][j] == 1) || (bolas[i][j] == 0)) {
						res = (res*2)+bolas[i][j];
					}
				}
			}
			return res;
		}
	}
	
	
	public ArrayList calcula_movimientos(Tablero tab) {
		ArrayList res = new ArrayList();
		int tablero[][] = tab.bolas;
		// Calcula los posibles movimientos
		// Elimina los movimientos que dan lugar a tableros equivalentes
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
				if (tablero[i][j] == 0) {
					if ((i>2) && (tablero[i-1][j] == 1) && (tablero[i-2][j] == 1)) {
						Movimiento posible = new Movimiento(i-2, j, i-1, j, i, j);
						comprobarMov(tab, posible, res);
					}
					if ((j>2) && (tablero[i][j-1] == 1) && (tablero[i][j-2] == 1)) {
						Movimiento posible = new Movimiento(i, j-2, i, j-1,i, j);
						comprobarMov(tab, posible, res);
					}
					if ((i<9) && (tablero[i+1][j] == 1) && (tablero[i+2][j] == 1)) {
						Movimiento posible = new Movimiento(i+2, j, i+1, j, i, j);
						comprobarMov(tab, posible, res);
					}
					if ((j<9) && (tablero[i][j+1] == 1) && (tablero[i][j+2] == 1)) {
						Movimiento posible = new Movimiento(i, j+2, i, j+1, i, j);
						comprobarMov(tab, posible, res);
					}
				}
			}
		}
		return res;
	}
	
	public Tablero rotar90(Tablero tab) {
		Tablero res = new Tablero();
		res.init();
		int tablero_in[][] = tab.bolas;
		int tablero_out[][] = res.bolas;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				tablero_out[i][j] = tablero_in[8-j][i];
			}
		}
		return res;
	}

    public int calculaValor(Tablero tab) {
        int res = 0;
		int tablero_in[][] = tab.bolas;
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
                if (tablero_in[i][j] == 1) {
                    res += pag.valores[i][j];
                }
			}
		}
        return res;
    }
	
	public int calculaDistanciasTablero(Tablero tab) {
        int res = 0;
		int tablero_in[][] = tab.bolas;
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
                if (tablero_in[i][j] == 1) {
                    //res += Math.abs(4-i) + Math.abs(4 -j);
                    
                    if (tablero_in[i-1][j] == 1) res++;
                    if (tablero_in[i][j-1] == 1) res++;
                    if (tablero_in[i+1][j] == 1) res++;
                    if (tablero_in[i][j+1] == 1) res++;
                    
                }
			}
		}
        /*
        if (res == 0) 
            return res;
        else 
            return (res/2)+1;
        */
        return res;
	}
	
	public void comprobarMov(Tablero tab, Movimiento posible, ArrayList res) {
		//TODO: Analizar como eliminar mas movimientos en base a la "dispersion" de las bolas
		realiza_movimiento(tab, posible);
		Tablero toCheck = rotar90(tab);
		if (tableros.get(toCheck.hashCode()) == null) {
			toCheck = rotar90(toCheck);
			if (tableros.get(toCheck.hashCode()) == null) {
				toCheck = rotar90(toCheck);
				if (tableros.get(toCheck.hashCode()) == null) {
					int hashcode = tab.hashCode();
					tableros.put(hashcode, hashcode);
					res.add(posible);
				}
			}
		}
		deshacer_movimiento(tab, posible);
	}
	
	public int cuantas(Tablero tab) {
		int res = 0;
		int tablero[][] = tab.bolas;
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
				if (tablero[i][j] == 1) res++;
			}
		}
		return res;
	}
	
	public void realiza_movimiento(Tablero tablero_in, Movimiento mov) {
		//System.out.println("Realizo mov : " + mov);
		tablero_in.bolas[mov.desde_x][mov.desde_y] = 0;
		tablero_in.bolas[mov.quito_x][mov.quito_y] = 0;
		tablero_in.bolas[mov.hasta_x][mov.hasta_y] = 1;
	}
	
	public void deshacer_movimiento(Tablero tablero_in, Movimiento mov) {
		//System.out.println("Deshago mov : " + mov);
		tablero_in.bolas[mov.desde_x][mov.desde_y] = 1;
		tablero_in.bolas[mov.quito_x][mov.quito_y] = 1;
		tablero_in.bolas[mov.hasta_x][mov.hasta_y] = 0;
	}

	public double posibles(ArrayList alternativas){
		double res = 1;
		for (int i = 0; i < alternativas.size(); i++) {
			res = res * new Integer("" + alternativas.get(i)).intValue();
		}
		return res;
	}
	
	public void recursivo(Tablero tab_in, ArrayList solucion, ArrayList alternativas) {
		Random randomGenerator = new Random();
		int bol = cuantas(tab_in);
		int hashCode = tab_in.hashCode();
        int distancia = calculaDistanciasTablero(tab_in);
		tested++;
		if (bol == 1) {
			System.out.println("SOLUCION : " + solucion + "\n" + tab_in);
			//System.exit(0);
		}
		if (inviables.get(hashCode) != null) {
			hits++;
			return;
		}
        if (calculaValor(tab_in) < 0) {
            //System.out.println("Inviable por distancia!!");
			//System.out.println("Bolas: " + bol + " Distancia: " + distancia);
			//System.out.println(tab_in);
    		inviables.put(hashCode, hashCode);
            return;
        }
		ArrayList posibles = calcula_movimientos(tab_in);
		if (bol % 22 == 0) {
			long elapsed = System.currentTimeMillis() - startTime;
			double toEnd = posibles(alternativas);
			double checkBySec = tested / (elapsed / 1000);
			if (elapsed > 0) {
				double minToEnd = (toEnd / checkBySec) / 60;
				System.out.println("-> " + tested + " " + hits + " " + (hits / tested) * 100 + "% \n" +
					toEnd + " " + checkBySec + " " + minToEnd / 60 + " h. " +
					minToEnd / 1440 + " d. " + minToEnd / 525600 + " y. " 
					+ " \n" + inviables.size() + " " + tableros.size());
			}
			//System.out.println("Sol " + solucion);
			System.out.println("Bolas: " + bol + " Distancia: " + distancia);
			System.out.println("Posibles " + posibles);
			System.out.println("Alternativas " + alternativas);
			System.out.println(tab_in);
		}
        int numPosibles = posibles.size();
		while (numPosibles != 0) {
            int mejor = 0;
            int cual = 0;
            for (int i = 0; i < numPosibles; i++) {
			    Movimiento mov = (Movimiento)posibles.get(i);
			    realiza_movimiento(tab_in, mov);
                int dist = calculaDistanciasTablero(tab_in);
                if (dist > mejor) {
                    mejor = dist;
                    cual = i;
                }
			    deshacer_movimiento(tab_in, mov);
            }
			//int randomInt = randomGenerator.nextInt(posibles.size());			
			//Movimiento mov = (Movimiento)posibles.get(randomInt);
			Movimiento mov = (Movimiento)posibles.get(cual);
			solucion.add(mov);
			alternativas.add(posibles.size());
			realiza_movimiento(tab_in, mov);
			recursivo(tab_in, solucion, alternativas);
			deshacer_movimiento(tab_in, mov);
			solucion.remove(solucion.size() - 1);
			alternativas.remove(alternativas.size() - 1);
			//posibles.remove(randomInt);
			posibles.remove(cual);
            numPosibles--;
            //try { Thread.sleep(1000);} catch (Exception ex) {}
		}
		//System.out.println("Retorno");
		inviables.put(hashCode, hashCode);
	}
	
	public void run() {
		Tablero tab = new Tablero();
		tab.init();
		ArrayList solucion = new ArrayList();
		ArrayList alternativas = new ArrayList();
		startTime = System.currentTimeMillis();
		recursivo(tab, solucion, alternativas);
	}

	public static void main(String[] args) {		
		Solitario sol = new Solitario();
		sol.run();
	}
}
