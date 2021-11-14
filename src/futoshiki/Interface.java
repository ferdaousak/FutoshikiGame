package futoshiki;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

import metier.Backtracking;
import metier.Graph;
import metier.SET;
import metier.ST;
import javax.swing.JCheckBox;

public class Interface extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*------- les cellules de notre jeux -------*/
	private InputText[][] cellules;
	/*------- Pour le traitement des symboles  */
	public char[][] contraintesHoriz,contraintesVert;
	
	/*---- le choix des algorithmes ----*/
	private JCheckBox checkbox_MRV;
	private JCheckBox chckbxDegree;
	private JCheckBox checkbox_LCV ;
	private JCheckBox checkbox_Fc ;
	private JCheckBox checkbox_Ac ;
	/*---- Affichage de temps d'execution ----*/
	private JLabel temps;
	
	public InputText[][] getCellules()
	{
		return cellules;
	}

	public void setCellules(InputText[][] cellules)
	{
		this.cellules = cellules;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Interface(String name)
	{
		/*------ Panel et d'autre variables--------*/
		super(name);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setEnabled(false);
		getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		
		getContentPane().add(panel, BorderLayout.NORTH);
		
		
		/*------- tous sur le cote d'interface( button, comboBox ..etc ----- */
		
		JPanel game_panel = new JPanel();
		JComboBox comboBox = new JComboBox();
		comboBox.setForeground(Color.BLUE);
		comboBox.setFont(new Font("SimSun", Font.BOLD | Font.ITALIC, 15));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"4x4", "5x5", "6x6", "7x7", "8x8", "9x9"}));
		comboBox.setSize(comboBox.getWidth()+50, comboBox.getHeight());
		panel.add(comboBox);
		
		JComboBox difficultyCombo = new JComboBox();
		difficultyCombo.setForeground(Color.BLUE);
		difficultyCombo.setFont(new Font("SimSun", Font.BOLD | Font.ITALIC, 11));
		difficultyCombo.setModel(new DefaultComboBoxModel(new String[] {"facile", "normale", "difficile"}));
		panel.add(difficultyCombo);
		
		JButton btn_Start = new JButton("Commencer");
		btn_Start.setForeground(Color.BLUE);
		btn_Start.setFont(new Font("SimSun", Font.BOLD | Font.ITALIC, 11));
		btn_Start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// il faut supprimer tout le grille avant de commencer 
				game_panel.removeAll();
				game_panel.repaint();
				
				//on prend la taille de grille d'apres le choix de l'utilisateur 
				String size = (String)comboBox.getSelectedItem();
				int n = size.charAt(0)-'0';

				//nombre totale des lignes et colonne 
				int l = (n*2)-1; 
				//La taille du notre panel
				int s = n*50 + (n-1)*35; 
				
				contraintesHoriz = new char[n][n-1];
				contraintesVert  = new char[n-1][n];
				
				game_panel.setLayout(null);
				game_panel.setPreferredSize(new Dimension(s,s));
				
				/*Le dessin de notre jeux */
				int X = game_panel.getX()+30;
				int Y = game_panel.getY();
				
				cellules = new InputText[l][l];
				
				for(int i=0;i<l;i++)
				{
					for(int j=0;j<l;j++)
					{	//si les lignes sont pair
						if(i%2==0)
						{
							//si les colonnes sont pair il faut qu'il soit un nombre  
							if(j%2==0)
							{  
								InputText nT = new InputText(InputText.NUMBER);
								nT.setBounds(X+5+j*30,Y+5+i*30,30,30);
								nT.setText("");
								nT.setFont(new Font("Tahoma", Font.BOLD, 13));
								nT.setHorizontalAlignment(SwingConstants.CENTER);
								
								//on l'ajoute au cellules 
								cellules[i][j] = nT;
								//on l'ajoute au panel
								game_panel.add(nT);
								
								
							}else //si les colonnes sont impair dont il faut qu'il soit des symbole horizontal
							{   
								InputText sT = new InputText(InputText.HORIZONTAL_SYMBOL);
								sT.setBounds(X+10+j*30,Y+5+i*30,20,20);
								sT.setText("");
								sT.setEditable(false);
								sT.setFont(new Font("Tahoma", Font.BOLD, 13));
								sT.setHorizontalAlignment(SwingConstants.CENTER);

								cellules[i][j] = sT;
								
								game_panel.add(sT);
							}
						}else
						{   //si le ligne et impair et le colonne pair on ajoute les symboles verticale
							if(j%2==0)
							{
								InputText sT = new InputText(InputText.VERTICAL_SYMBOL);
								sT.setBounds(X+5+j*30,Y+10+i*30,20,20);
								sT.setText("");
								sT.setEditable(false);
								sT.setFont(new Font("Tahoma", Font.BOLD, 13));
								sT.setHorizontalAlignment(SwingConstants.CENTER);
								
								cellules[i][j] = sT;
								
								game_panel.add(sT);
							}else // pour les lignes impair et colonnes impaire il doit etre vide 
							{
								InputText vT = new InputText(InputText.EMPTY);
								cellules[i][j] = vT;
							}
						}
					}
				}
				
				/*------- Remplissage des cases ---------*/
				switch(comboBox.getSelectedIndex())
				{
				//pour 4x4
					case 0:
						if(difficultyCombo.getSelectedIndex() == 0) //facile
						{
							cellules[0][1].setText("<");
			                cellules[1][4].setText("^");
			                cellules[1][6].setText("^");
			                cellules[6][1].setText("<");
			                cellules[5][6].setText("^");
			                cellules[6][6].setText("2");
						}
						if(difficultyCombo.getSelectedIndex() == 1) //normale
						{
							cellules[0][2].setText("3");
							cellules[1][4].setText("v");
							cellules[3][4].setText("v");
							cellules[3][6].setText("v");
							cellules[5][0].setText("^");
							cellules[6][5].setText(">");
						}
						if(difficultyCombo.getSelectedIndex() == 2) //difficile 
						{
							cellules[0][0].setText("3");
							cellules[0][6].setText("1");
							cellules[3][2].setText("^");
							cellules[3][4].setText("^");
							cellules[6][5].setText(">");
						}
						
						break;
					//pour 5x5
					case 1:
						if(difficultyCombo.getSelectedIndex() == 0) //facile
						{
							cellules[0][3].setText(">");
							cellules[0][7].setText("<");
			                cellules[4][3].setText(">");
			                cellules[5][4].setText("v");
			                cellules[6][0].setText("4");
			                cellules[6][5].setText(">");
			                cellules[8][1].setText(">");
			                cellules[8][2].setText("3");
						}
						if(difficultyCombo.getSelectedIndex() == 1) //difficile 
						{
							cellules[0][0].setText("3");
							cellules[0][1].setText(">");
			                cellules[2][1].setText("<");
			                cellules[3][8].setText("v");
			                cellules[4][5].setText(">");
			                cellules[5][6].setText("v");
			                cellules[6][2].setText("1");
			                cellules[8][7].setText(">");
			                cellules[8][8].setText("3");
						}
						if(difficultyCombo.getSelectedIndex() == 2) //normale 
						{
							cellules[0][1].setText("<");
							cellules[0][6].setText("2");
							cellules[2][3].setText(">");
							cellules[3][4].setText("v");
							cellules[4][1].setText(">");
							cellules[6][1].setText(">");
							cellules[6][5].setText("<");
							cellules[6][8].setText("4");
							cellules[7][4].setText("v");
							cellules[8][5].setText("<");
						}
						break;
					//pour 6x6
					case 2:
						if(difficultyCombo.getSelectedIndex() == 0) //facile 
						{
							cellules[0][5].setText(">");
							cellules[0][6].setText("4");
							cellules[1][2].setText("^");
							cellules[1][10].setText("^");
							cellules[3][2].setText("v");
							cellules[4][7].setText(">");
							cellules[4][9].setText(">");
							cellules[5][6].setText("^");
							cellules[6][1].setText(">");
							cellules[6][9].setText(">");
							cellules[7][10].setText("v");
							cellules[8][7].setText(">");
							cellules[8][9].setText(">");
							cellules[9][2].setText("^");
							cellules[10][5].setText("<");
							cellules[10][9].setText(">");
							
						}
						if(difficultyCombo.getSelectedIndex() == 1) //difficile 
						{
							cellules[1][0].setText("v");
							cellules[2][2].setText("4");
							cellules[2][3].setText("<");
							cellules[2][9].setText(">");
							cellules[4][0].setText("4");
							cellules[4][1].setText(">");
							cellules[4][4].setText("1");
							cellules[5][2].setText("v");
							cellules[5][8].setText("^");
							cellules[6][0].setText("6");
							cellules[6][7].setText("<");
							cellules[7][8].setText("^");
							cellules[8][9].setText("<");
							cellules[10][1].setText(">");
							cellules[10][10].setText("2");
							
						}
						if(difficultyCombo.getSelectedIndex() == 2) //normale
						{
							cellules[1][0].setText("v");
							cellules[1][6].setText("^");
							cellules[2][5].setText("<");
							cellules[3][2].setText("v");
							cellules[3][8].setText("^");
							cellules[4][9].setText(">");
							cellules[5][2].setText("v");
							cellules[5][4].setText("^");
							cellules[5][8].setText("^");
							cellules[6][5].setText("<");
							cellules[7][2].setText("v");
							cellules[8][5].setText("<");
							cellules[8][9].setText(">");
							cellules[9][0].setText("v");
							cellules[9][8].setText("^");
							cellules[10][0].setText("5");
							cellules[10][6].setText("1");
							cellules[10][9].setText(">");
						}
						break;
					//pour 7x7
					case 3 :
						if(difficultyCombo.getSelectedIndex() == 0)
						{
							cellules[0][0].setText("6");
							cellules[0][1].setText(">");
							cellules[1][2].setText("v");
							cellules[1][10].setText("v");
							cellules[2][1].setText("<");
							cellules[2][3].setText(">");
							cellules[2][11].setText(">");
							cellules[3][2].setText("v");
							cellules[3][6].setText("v");
							cellules[4][4].setText("5");
							cellules[5][8].setText("^");
							cellules[5][12].setText("v");
							cellules[6][7].setText(">");
							cellules[8][8].setText("3");
							cellules[9][10].setText("v");
							cellules[10][1].setText(">");
							cellules[10][4].setText("3");
							cellules[10][6].setText("7");
							cellules[10][9].setText(">");
							cellules[11][0].setText("v");
							cellules[11][4].setText("v");
							cellules[11][8].setText("^");
							cellules[11][10].setText("v");
							
						}
						if(difficultyCombo.getSelectedIndex() == 1)
						{
							cellules[0][9].setText("<");
							cellules[2][4].setText("2");
							cellules[2][9].setText("<");
							cellules[3][0].setText("^");
							cellules[3][2].setText("^");
							cellules[3][4].setText("v");
							cellules[6][2].setText("6");
							cellules[6][3].setText("<");
							cellules[6][7].setText("<");
							cellules[6][11].setText("<");
							cellules[7][0].setText("v");
							cellules[7][2].setText("^");
							cellules[8][7].setText("<");
							cellules[8][8].setText("5");
							cellules[8][9].setText(">");
							cellules[10][0].setText("4");
							cellules[10][1].setText("<");
							cellules[10][5].setText(">");
							cellules[10][11].setText("<");
							cellules[12][0].setText("5");
							cellules[12][4].setText("3");
							cellules[12][9].setText("<");
						}
						if(difficultyCombo.getSelectedIndex() == 2)
						{
							cellules[0][8].setText("4");
							cellules[0][11].setText(">");
							cellules[0][12].setText("3");
							cellules[1][2].setText("v");
							cellules[1][4].setText("v");
							cellules[1][6].setText("^");
							cellules[3][0].setText("^");
							cellules[3][2].setText("v");
							cellules[4][2].setText("3");
							cellules[4][5].setText("<");
							cellules[4][11].setText(">");
							cellules[4][12].setText("2");
							cellules[5][4].setText("v");
							cellules[6][1].setText("<");
							cellules[6][6].setText("1");
							cellules[6][10].setText("3");
							cellules[8][9].setText("<");
							cellules[9][4].setText("v");
							cellules[9][6].setText("^");
							cellules[9][8].setText("^");
							cellules[10][2].setText("5");
							cellules[12][5].setText(">");
							cellules[12][6].setText("4");
							cellules[12][9].setText("<");
							cellules[12][11].setText("<");
						}
						
						break;
					//pour 8x8
					case 4 :
						if(difficultyCombo.getSelectedIndex() == 0)
						{
							cellules[0][0].setText("6");
							cellules[0][1].setText(">");
							cellules[0][5].setText(">");
							cellules[1][4].setText("^");
							cellules[1][6].setText("^");
							cellules[2][9].setText(">");
							cellules[3][0].setText("v");
							cellules[3][8].setText("^");
							cellules[4][1].setText("<");
							cellules[4][3].setText("<");
							cellules[4][7].setText(">");
							cellules[4][11].setText(">");
							cellules[4][13].setText("<");
							cellules[5][0].setText("v");
							cellules[7][14].setText("^");
							cellules[8][1].setText(">");
							cellules[8][3].setText("<");
							cellules[8][7].setText(">");
							cellules[9][8].setText("v");
							cellules[9][10].setText("v");
							cellules[10][5].setText(">");
							cellules[10][10].setText("3");
							cellules[10][13].setText(">");
							cellules[11][2].setText("^");
							cellules[11][6].setText("v");
							cellules[11][12].setText("^");
							cellules[12][3].setText(">");
							cellules[12][7].setText(">");
							cellules[12][12].setText("5");
							cellules[13][2].setText("^");
							cellules[13][12].setText("v");
							cellules[14][0].setText("3");
							cellules[14][2].setText("5");
							cellules[14][3].setText(">");
							cellules[14][5].setText(">");
							cellules[14][9].setText("<");
							cellules[14][10].setText("7");
						}
						if(difficultyCombo.getSelectedIndex() == 1)
						{
							cellules[0][3].setText("<");
							cellules[0][5].setText("<");
							cellules[0][6].setText("6");
							cellules[0][9].setText(">");
							cellules[0][12].setText("2");
							cellules[1][0].setText("^");
							cellules[1][8].setText("^");
							cellules[1][12].setText("v");
							cellules[2][4].setText("6");
							cellules[2][5].setText(">");
							cellules[2][9].setText("<");
							cellules[4][5].setText(">");
							cellules[4][11].setText(">");
							cellules[5][10].setText("^");
							cellules[5][12].setText("v");
							cellules[6][3].setText(">");
							cellules[6][7].setText(">");
							cellules[7][0].setText("v");
							cellules[7][2].setText("v");
							cellules[7][8].setText("^");
							cellules[7][14].setText("v");
							cellules[8][3].setText(">");
							cellules[8][9].setText("<");
							cellules[9][0].setText("v");
							cellules[9][14].setText("^");
							cellules[10][1].setText("<");
							cellules[11][4].setText("v");
							cellules[12][9].setText("<");
							cellules[13][6].setText("v");
							cellules[13][12].setText("^");
							cellules[14][1].setText("<");
							cellules[14][3].setText("<");
							cellules[14][10].setText("2");
							cellules[14][12].setText("5");
							cellules[14][13].setText(">");
						}
						if(difficultyCombo.getSelectedIndex() == 2)
						{
							cellules[0][3].setText("<");
							cellules[0][5].setText("<");
							cellules[0][6].setText("6");
							cellules[0][9].setText(">");
							cellules[0][12].setText("2");
							cellules[1][0].setText("^");
							cellules[1][8].setText("^");
							cellules[2][4].setText("6");
							cellules[2][5].setText(">");
							cellules[2][9].setText("<");
							cellules[4][5].setText(">");
							cellules[4][11].setText(">");
							cellules[5][10].setText("^");
							cellules[5][12].setText("v");
							cellules[6][3].setText(">");
							cellules[6][7].setText(">");
							cellules[7][0].setText("v");
							cellules[7][2].setText("v");
							cellules[7][8].setText("^");
							cellules[7][14].setText("v");
							cellules[8][3].setText(">");
							cellules[8][9].setText("<");
							cellules[9][0].setText("v");
							cellules[9][14].setText("^");
							cellules[10][1].setText("<");
							cellules[11][4].setText("v");
							cellules[12][9].setText("<");
							cellules[13][6].setText("v");
							cellules[13][12].setText("^");
							cellules[14][1].setText("<");
							cellules[14][3].setText("<");
							cellules[14][10].setText("2");
							cellules[14][12].setText("5");
							cellules[14][13].setText(">");
							
						}
						
						break;
					//pour 9x9	
					case 5 :
						if(difficultyCombo.getSelectedIndex() == 0)
						{
							cellules[0][1].setText(">");
							cellules[0][2].setText("6");
							cellules[0][9].setText(">");
							cellules[0][16].setText("5");
							cellules[1][0].setText("^");
							cellules[1][6].setText("v");
							cellules[1][12].setText("v");
							cellules[2][7].setText(">");
							cellules[2][15].setText("<");
							cellules[3][2].setText("^");
							cellules[3][12].setText("v");
							cellules[4][5].setText(">");
							cellules[4][7].setText("<");
							cellules[4][12].setText("3");
							cellules[5][0].setText("^");
							cellules[5][6].setText("v");
							cellules[6][0].setText("2");
							cellules[6][5].setText("<");
							cellules[6][7].setText(">");
							cellules[6][11].setText("<");
							cellules[6][13].setText(">");
							cellules[6][15].setText(">");
							cellules[7][4].setText("v");
							cellules[7][6].setText("^");
							cellules[7][12].setText("^");
							cellules[7][16].setText("v");
							cellules[8][15].setText("<");
							cellules[9][0].setText("^");
							cellules[9][2].setText("v");
							cellules[9][6].setText("^");
							cellules[10][2].setText("4");
							cellules[10][4].setText("6");
							cellules[10][7].setText("<");
							cellules[11][16].setText("v");
							cellules[12][11].setText(">");
							cellules[13][4].setText("^");
							cellules[13][6].setText("^");
							cellules[13][10].setText("^");
							cellules[13][16].setText("^");
							cellules[14][9].setText(">");
							cellules[14][11].setText("<");
							cellules[15][16].setText("^");

						}
						if(difficultyCombo.getSelectedIndex() == 1)
						{
							cellules[0][0].setText("5");
							cellules[0][7].setText(">");
							cellules[1][2].setText("v");
							cellules[1][10].setText("v");
							cellules[1][12].setText("v");
							cellules[1][14].setText("v");
							cellules[2][11].setText(">");
							cellules[3][0].setText("v");
							cellules[3][4].setText("v");
							cellules[3][14].setText("v");
							cellules[4][3].setText("<");
							cellules[4][7].setText("<");
							cellules[4][9].setText(">");
							cellules[4][15].setText(">");
							cellules[6][7].setText("<");
							cellules[7][0].setText("^");
							cellules[7][4].setText("v");
							cellules[7][10].setText("v");
							cellules[7][14].setText("^");
							cellules[8][2].setText("7");
							cellules[8][3].setText("<");
							cellules[8][7].setText("<");
							cellules[8][13].setText(">");
							cellules[9][0].setText("^");
							cellules[9][8].setText("^");
							cellules[9][10].setText("v");
							cellules[10][2].setText("5");
							cellules[10][5].setText(">");
							cellules[10][11].setText(">");
							cellules[11][4].setText("v");
							cellules[11][16].setText("^");
							cellules[12][5].setText(">");
							cellules[12][7].setText(">");
							cellules[12][11].setText(">");
							cellules[12][12].setText("6");
							cellules[13][12].setText("v");
							cellules[14][4].setText("3");
							cellules[15][0].setText("^");
							cellules[15][4].setText("^");
							cellules[15][10].setText("^");
							cellules[15][14].setText("^");
							cellules[16][8].setText("9");
							cellules[16][14].setText("4");
								
						}
						if(difficultyCombo.getSelectedIndex() == 2)
						{
							cellules[0][0].setText("8");
							cellules[0][2].setText("2");
							cellules[0][9].setText(">");
							cellules[0][15].setText("<");
							cellules[1][8].setText("^");
							cellules[2][1].setText(">");
							cellules[2][5].setText(">");
							cellules[2][8].setText("5");
							cellules[3][6].setText("v");
							cellules[4][2].setText("6");
							cellules[4][6].setText("2");
							cellules[4][16].setText("8");
							cellules[5][4].setText("v");
							cellules[6][6].setText("4");
							cellules[7][8].setText("^");
							cellules[7][10].setText("^");
							cellules[7][16].setText("^");
							cellules[8][8].setText("4");
							cellules[9][10].setText("^");
							cellules[10][3].setText("<");
							cellules[11][4].setText("^");
							cellules[11][6].setText("^");
							cellules[11][10].setText("v");
							cellules[11][12].setText("^");
							cellules[11][16].setText("^");
							cellules[12][3].setText(">");
							cellules[12][4].setText("6");
							cellules[13][0].setText("^");
							cellules[13][8].setText("^");
							cellules[14][0].setText("2");
							cellules[14][2].setText("4");
							cellules[14][3].setText(">");
							cellules[14][7].setText("<");
							cellules[14][10].setText("9");
							cellules[14][16].setText("1");
							cellules[15][8].setText("^");
							cellules[16][0].setText("5");
							cellules[16][1].setText(">");
							cellules[12][7].setText("<");
							cellules[12][8].setText("7");
							cellules[12][9].setText(">");
							cellules[12][11].setText(">");
						}
						break;
					
				}
				
                // parcourir les case pour que les cases vide et symbole soit unchangeable
				for(int i=0;i<cellules.length;i++)
					for(int j=0;j<cellules.length;j++)
					{
						if(!cellules[i][j].getText().isEmpty())
							cellules[i][j].setEditable(false);
						
						if(cellules[i][j].getType() == InputText.HORIZONTAL_SYMBOL || cellules[i][j].getType() == InputText.VERTICAL_SYMBOL)
							cellules[i][j].setBackground(new java.awt.Color(166, 189, 219));
					}
							
				
				
				
				
				game_panel.repaint();
				game_panel.revalidate();
				
				//l'affichage de temps d'execution
				temps = new JLabel("Temps : ");
				temps.setBounds(game_panel.getX()+50,game_panel.getY()+s-(s/3),100,100);
				temps.setVisible(false);
				
				game_panel.add(temps);
				
				getContentPane().add(game_panel, BorderLayout.CENTER);
				pack();
			}
		});
		panel.add(btn_Start);
		
		/*------- le btn de verification du solution ------*/
		
		
		/*-----------------btn de solution---------------------*/
		
		JLabel lblNewLabel = new JLabel("ALgos : ");
		lblNewLabel.setForeground(Color.RED);
		lblNewLabel.setFont(new Font("SimSun", Font.BOLD, 11));
		panel.add(lblNewLabel);
		
		checkbox_MRV = new JCheckBox("MRV");
		panel.add(checkbox_MRV);
		
		chckbxDegree = new JCheckBox("Degree");
		panel.add(chckbxDegree);
		
		checkbox_LCV = new JCheckBox("LCV");
		panel.add(checkbox_LCV);
		
		checkbox_Fc = new JCheckBox("FC");
		panel.add(checkbox_Fc);
		
		checkbox_Ac = new JCheckBox("AC");
		panel.add(checkbox_Ac);
		JButton btn_Solve = new JButton("Solution");
		btn_Solve.setForeground(Color.BLUE);
		btn_Solve.setFont(new Font("SimSun", Font.BOLD | Font.ITALIC, 11));
		btn_Solve.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				float startTime = System.nanoTime();
				
				solution();
				float endTime = System.nanoTime();
			 	
				double time = (endTime - startTime) * Math.pow(10, -9);
				
				temps.setText("Temps : "+ String.format("%.2f", time) + " s");
				temps.setVisible(true);
				
				game_panel.repaint();
				game_panel.revalidate();
				pack();
				setVisible(true);
			}
		});
		panel.add(btn_Solve);
		JButton btn_Check = new JButton("Verifier");
		btn_Check.setForeground(Color.BLUE);
		btn_Check.setFont(new Font("SimSun", Font.BOLD | Font.ITALIC, 11));
		btn_Check.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				boolean success = check(cellules);
				if(success)
					JOptionPane.showMessageDialog(null, "Votre solution est : "+ (success? "correct!!" : "fausse") );
			}
		});
		panel.add(btn_Check);
		
		game_panel.repaint();
		game_panel.revalidate();
		pack();
		setVisible(true);
	}
	
	// fonction de verification de solution de l'utilisateur
	public boolean check(InputText[][] cellules)
	{
		for(int i=0; i<cellules.length; i++)
		{
			for(int j=0; j<cellules.length;j++)
			{		// si les cellules sont vides
				if(cellules[i][j].getType() == InputText.NUMBER)
				{
					if(cellules[i][j].getText().isEmpty())
					{
						JOptionPane.showMessageDialog(null, "cellule "+i+" , "+j + " est vide!");
						return false;
					}
					
					try
					{	// si la valeur ajouter est sup au taille de grille ou inf a 1
						int val = Integer.parseInt(cellules[i][j].getText());
						if(val > (cellules.length+1)/2 || val < 1)
						{
							JOptionPane.showMessageDialog(null, "valeur "+val+" at cellule "+i+" , "+j + " est invalid!!");
							return false;
						}
						if(j<cellules.length-1)
						{ 		// si il existe une valeur dans le meme ligne et colonne 
							for(int k=j+2;k<cellules.length;k+=2)
							{
								int val2 = Integer.parseInt(cellules[i][k].getText());
								if(val == val2)
								{
									JOptionPane.showMessageDialog(null, "valeur "+val+" at cellule "+i+" , "+j + " existe dï¿½ja dans la mï¿½me ligne!!");
									return false;
								}
							}	
						}
						if(i<cellules.length-1)
						{
							for(int k=i+2;k<cellules.length;k+=2)
							{
								int val2 = Integer.parseInt(cellules[k][j].getText());
								if(val == val2)
								{
									JOptionPane.showMessageDialog(null, "valeur "+val+" at cellule "+i+" , "+j + " existe dï¿½ja dans la mï¿½me colonne!!");
									return false;
								}
										
							}
						}
						
					}catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
					
				}
				
				// verification des contraine de sup et inf 
				if((cellules[i][j].getType() == InputText.HORIZONTAL_SYMBOL || cellules[i][j].getType() == InputText.VERTICAL_SYMBOL) && !cellules[i][j].getText().isEmpty())
				{	//on switch le type de notre cellule
					switch(cellules[i][j].getType())
					{		// pour les sympol horiz
					case InputText.HORIZONTAL_SYMBOL : 
						boolean condH = cellules[i][j].getText().equals(">");
						try 
						{	// c'est deux valeur coté a coté 
							int val1 = Integer.parseInt(cellules[i][j-1].getText());
							int val2 = Integer.parseInt(cellules[i][j+1].getText());
							
							System.out.println("val "+ val1 + "  val2 : " + val2);
							
							System.out.println("s == '>' ? : "+condH);
							// condition de sup et inf
							if( (condH && (val1 < val2)) || (!condH && (val1 > val2)) )
							{
								JOptionPane.showMessageDialog(null, val1+" at cellule "+i+","+(j-1) + "doit ï¿½tre "+cellules[i][j].getText()+" ï¿½ "+val2+" at cellule "+i+","+(j+1) + " !!!");
								return false;
							}
						}catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
						break;
					case InputText.VERTICAL_SYMBOL : // pour les sympol Vert
						boolean condV = cellules[i][j].getText().equals("v");
						try 
						{	// les deux variable de haut et bas
							int val1 = Integer.parseInt(cellules[i-1][j].getText());
							int val2 = Integer.parseInt(cellules[i+1][j].getText());
							System.out.println("val "+ val1 + "  val2 : " + val2);
							//condition de sup et inf
							if( (condV && (val1 < val2)) || (!condV && (val1 > val2)) )
							{
								JOptionPane.showMessageDialog(null, val1+" at cellule "+i+","+(j-1) + "doit ï¿½tre "+cellules[i][j].getText()+" ï¿½ "+val2+" at cellule "+i+","+(j+1) + " !!!");
								return false;
							}
						}catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
						break;
						default : System.out.println("err"); return false;
					}
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public void solution()
	{
	       int l = cellules.length;
	       int n = (l+1)/2;  //taille du jeu
	       //Creer le graphe
	       Graph g = new Graph();
	       
	       // containtes de symboles < > v ^
	       for(int i=0;i<l;i++)
	       {
	    	   for(int j=0;j<l;j++)
	    	   {	
	    		   if(cellules[i][j].getType()==InputText.HORIZONTAL_SYMBOL && !cellules[i][j].getText().isEmpty())
	    		   {	// i/2 et j/2 car note grille et plus grand que les cellules de notre jeu 
    				   contraintesHoriz[i/2][j/2] = cellules[i][j].getText().charAt(0);
	    		   }	// i/2 et j/2 car note grille et plus grand que les cellules de notre jeu
	    		   if(cellules[i][j].getType()==InputText.VERTICAL_SYMBOL && !cellules[i][j].getText().isEmpty())
	    		   {
	    			   contraintesVert[i/2][j/2] = cellules[i][j].getText().charAt(0);
	    		   } 
	    	   }
	       }
	       
	       // contrainte lignes & colonnes ( on ajoute tous les valuer dans les meme boucle )
	       for(int i=0;i<n;i++)
	       {
	    	   for(int j=0;j<n-1;j++)
	    	   {
	    		   for(int k=j+1; k<n;k++)
	    		   {
	    			   String val1 = "x"+i+""+j;
	        		   String val2 = "x"+i+""+k;
	        		   String val3 = "x"+j+""+i;
	        		   String val4 = "x"+k+""+i;
	        		   
	    			   g.addEdge(val1, val2);
	    			   g.addEdge(val3, val4);
	    		   }
	    	   }
	       }
	       
	       
	       for(int i=0;i<n;i++)
	       {
	    	for(int j=0;j<n;j++)
	    	{
	    		if( (i< n-1) && (contraintesVert[i][j]=='^' || contraintesVert[i][j] == 'v'))
	    		{
	    			// si la condition est V 
	    			boolean cond = contraintesVert[i][j] == 'v';
	    			
	    			String val1 = cond? "s" + i + "" + j : "s" + (i+1) + "" + j;
	    			String val2 = cond? "x" + (i+1) + "" + j : "x" + i + "" + j;
	    			//ajouter au graphe 
	    			g.addEdge(val1, val2);
	    			
	    			val1 = val1.replace("s", "x");
	    			val2 = val2.replace("x", "i");
	    			//ajouter au graphe 
	    			g.addEdge(val1, val2);
	    		}
	    		
	    		if(( j < n-1 ) && (contraintesHoriz[i][j]=='>' || contraintesHoriz[i][j] == '<'))
	    		{
	    			//si la condition est >
	    			boolean cond = contraintesHoriz[i][j] == '>';
	    			
	    			String val1 = cond? "s" + i + "" + j : "s" + i + "" + (j+1);
	    			String val2 = cond? "x" + i + "" + (j+1) : "x" + i + "" + j;

	    			//ajouter au graphe 
	    			g.addEdge(val1, val2);

	    			val1 = val1.replace("s", "x");
	    			val2 = val2.replace("x", "i");
	    			//ajouter au graphe 
	    			g.addEdge(val1, val2);
	    			
	    		}
	    	}
	       }
	       
	    // Tables des domaines
	       ST<String, SET<String>> domainTable = new ST<String, SET<String>>();

	    // Remplisage des Domaines
	       
	       
	       Object[][] domains = new Object[n][n]; // par exemple (4x4)
	       
	       for(int i=0;i<l;i++)
	       {
	    	   for(int j=0;j<l;j++)
	    	   {
	    		   if(!cellules[i][j].getText().isEmpty() && cellules[i][j].getType()==InputText.NUMBER)
	    		   {
	    			   
	    			   domains[i/2][j/2] = new SET<String>();
	    			   // L'ajout de domaine avec une seule valeur (case remplie) 
	                   ((SET<String>)domains[i/2][j/2]).add(cellules[i][j].getText()); 
	    		   }
	    		   else if(cellules[i][j].getText().isEmpty() && cellules[i][j].getType()==InputText.NUMBER)
	    		   {
	    			   domains[i/2][j/2] = new SET<String>();
	    			   for(int k=1; k<=n; k++)
    				   {
	    				   ((SET<String>)domains[i/2][j/2]).add(""+k);
    				   }
	    		   } 
	    	   }
	       }

	       //ajouter les domaine aux noeuds
	       for(int i=0;i<n;i++)       
	       {
	    	   for(int j=0;j<n;j++)
	    	   {
	    		   domainTable.put("x"+i+""+j, ((SET<String>)domains[i][j]));
	    	   }
	               
	       }
	       
	       
	       //creeer la config
	       ST<String, String> config = new ST<String, String>();
	       
	       for(int i=0;i<n;i++)       // Ligne 
	    	   for(int j=0;j<n;j++)   // Colonne
	           config.put("x"+i+""+j,""); // Variables non affectï¿½es
	       
	       System.out.println("\nCalcul en cours ... "); 
	       Backtracking backtracking = new Backtracking(this);
	       
	       backtracking.WITHMRV =  checkbox_MRV.isSelected();        
	       backtracking.WITHDEGRES = chckbxDegree.isSelected();       
	       backtracking.WITHLCV = checkbox_LCV.isSelected();       
	       backtracking.WITHFC = checkbox_Fc.isSelected();         
	       backtracking.WITHAC1 = checkbox_Ac.isSelected();
	       
	       ST<String, String> result = backtracking.backtracking(config, domainTable, g);

	       //ST<String, String> result = backtracking.backtracking(AIType.getSelectedIndex(),config, domainTable, g);
	       // Afficher la solution
	       for(int i=0;i<n;i++)
	       {
	    	   for(int j=0;j<n;j++)
	    	   {
	    		   cellules[i*2][j*2].setText(config.get("x"+i+""+j));
	    	   }
	       }
	}

}
