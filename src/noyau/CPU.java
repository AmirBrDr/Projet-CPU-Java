package noyau;

import assembleur.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente le processeur central (CPU) du simulateur.
 * <p>
 * Cette classe orchestre l'exécution des instructions du programme.
 * Elle contient la mémoire, la banque de registres et l'unité arithmétique
 * et logique (ALU), ainsi que le compteur de programme (PC) indiquant
 * la prochaine instruction à exécuter.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class CPU {
    private Memoire memoire;
    private BanqueRegistres banqueRegistres;
    private ALU alu;
    private int pc;
    private boolean enExecution;

    /**
     * Construit une nouvelle instance de CPU.
     * Initialise la mémoire, la banque de registres, l'ALU,
     * et met le compteur de programme (PC) à zéro.
     */
    public CPU() {
        memoire = new Memoire();
        banqueRegistres = new BanqueRegistres();
        alu = new ALU();
        pc = 0;
        enExecution = false;
    }

    /**
     * Retourne la banque de registres associée à ce CPU.
     *
     * @return l'instance de {@link BanqueRegistres} utilisée
     */
    public BanqueRegistres getBanqueRegistres() {
        return banqueRegistres;
    }

    /**
     * Retourne la mémoire associée à ce CPU.
     *
     * @return l'instance de {@link Memoire} utilisée
     */
    public Memoire getMemoire() {
        return memoire;
    }

    /**
     * Retourne la valeur actuelle du compteur de programme (PC).
     *
     * @return l'adresse de la prochaine instruction à exécuter
     */
    public int getPc() {
        return pc;
    }

    /**
     * Indique si le CPU est actuellement en cours d'exécution.
     *
     * @return {@code true} si en cours d'exécution, {@code false} sinon
     */
    public boolean isEnExecution() {
        return enExecution;
    }

    /**
     * Lance l'exécution continue du programme chargé en mémoire.
     * Le processeur s'arrête lorsqu'il rencontre une instruction BREAK.
     */
    public void executerProgramme(){
        enExecution = true;
        while (enExecution){
            executerInstruction();
        }
    }

    /**
     * Exécute une seule instruction.
     * Décode l'instruction pointée par le compteur de programme (PC)
     * puis l'applique.
     */
    public void executerInstruction(){
        Instruction instruction = decoderInstruction();
        appliquerInstruction(instruction);
    }

    /**
     * Lit le prochain code d'opération (opcode) en mémoire
     * et incrémente le compteur de programme.
     *
     * @return le code de l'opération lu
     */
    public byte lireOpocode(){
        byte opocode = memoire.lireOctet(pc);
        pc++;
        return opocode;
    }

    /**
     * Lit l'octet suivant en mémoire et incrémente le compteur de programme.
     * Utilisé généralement pour lire les opérandes tels que les numéros de registres.
     *
     * @return l'octet lu
     */
    public byte lireOctetSuivant(){
        byte octet = memoire.lireOctet(pc);
        pc++;
        return octet;
    }

    /**
     * Lit l'adresse suivante en mémoire sur deux octets
     * et incrémente le compteur de programme de 2.
     *
     * @return l'adresse sur 16 bits reconstituée
     */
    public int lireAdresseSuivante(){
        int haute = memoire.lireOctet(pc) & 0xFF;
        int basse = memoire.lireOctet(pc + 1) & 0xFF;
        pc += 2;
        return (haute << 8) | basse;
    }

    /**
     * Charge un programme dans la mémoire du processeur à partir de l'adresse 0.
     * Réinitialise la mémoire avant de charger le programme.
     *
     * @param programme le {@link Programme} contenant les instructions à charger
     */
    public void chargerProgramme(Programme programme){
        memoire.vider();
        pc = 0;
        int adresse = 0;
        for (Instruction instruction : programme.getInstructions()) {
            switch (instruction.getTypeInstruction()) {
                case LOAD_CONSTANTE -> {
                    // opcode=1, reg, val  → 3 octets
                    memoire.ecrireOctet(adresse++, (byte) 1);
                    OperandeRegistre reg = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeConstante cst = (OperandeConstante) instruction.getOperandes().get(1);
                    memoire.ecrireOctet(adresse++, (byte) reg.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) cst.getValeur());
                }
                case LOAD_MEMOIRE -> {
                    // opcode=2, reg, adr(2 octets) → 4 octets
                    memoire.ecrireOctet(adresse++, (byte) 2);
                    OperandeRegistre reg = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeAdresse adr = (OperandeAdresse) instruction.getOperandes().get(1);
                    memoire.ecrireOctet(adresse++, (byte) reg.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresse() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresse() & 0xFF));
                }
                case STORE_MEMOIRE -> {
                    // opcode=3, reg, adr(2 octets) → 4 octets
                    memoire.ecrireOctet(adresse++, (byte) 3);
                    OperandeRegistre reg = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeAdresse adr = (OperandeAdresse) instruction.getOperandes().get(1);
                    memoire.ecrireOctet(adresse++, (byte) reg.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresse() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresse() & 0xFF));
                }
                case ADD -> {
                    // opcode=4, reg1, reg2, regDest → 4 octets
                    memoire.ecrireOctet(adresse++, (byte) 4);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case SUB -> {
                    memoire.ecrireOctet(adresse++, (byte) 5);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case MUL -> {
                    memoire.ecrireOctet(adresse++, (byte) 6);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case DIV -> {
                    memoire.ecrireOctet(adresse++, (byte) 7);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case AND -> {
                    memoire.ecrireOctet(adresse++, (byte) 8);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case OR -> {
                    memoire.ecrireOctet(adresse++, (byte) 9);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case XOR -> {
                    memoire.ecrireOctet(adresse++, (byte) 10);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeRegistre rd = (OperandeRegistre) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) rd.getNumeroRegistre());
                }
                case JUMP -> {
                    // opcode=11, adr(2 octets) → 3 octets
                    memoire.ecrireOctet(adresse++, (byte) 11);
                    OperandeAdresse adr = (OperandeAdresse) instruction.getOperandes().get(0);
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresse() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresse() & 0xFF));
                }
                case BEQ -> {
                    // opcode=12, reg1, reg2, adr(2 octets) → 5 octets
                    memoire.ecrireOctet(adresse++, (byte) 12);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeAdresse adr = (OperandeAdresse) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresse() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresse() & 0xFF));
                }
                case BNE -> {
                    memoire.ecrireOctet(adresse++, (byte) 13);
                    OperandeRegistre r1 = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeRegistre r2 = (OperandeRegistre) instruction.getOperandes().get(1);
                    OperandeAdresse adr = (OperandeAdresse) instruction.getOperandes().get(2);
                    memoire.ecrireOctet(adresse++, (byte) r1.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) r2.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresse() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresse() & 0xFF));
                }
                case BREAK -> {
                    memoire.ecrireOctet(adresse++, (byte) 0);
                }
                case DATA -> {
                    assembleur.OperandeDonnees data = (assembleur.OperandeDonnees) instruction.getOperandes().get(0);
                    for (int val : data.getValeurs()) {
                        memoire.ecrireOctet(adresse++, (byte) val);
                    }
                }
                case STRING -> {
                    assembleur.OperandeChaine chaine = (assembleur.OperandeChaine) instruction.getOperandes().get(0);
                    byte[] bytes = chaine.getValeur().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    for (byte b : bytes) {
                        memoire.ecrireOctet(adresse++, b);
                    }
                }
                case LOAD_INDEXE -> {
                    memoire.ecrireOctet(adresse++, (byte) 14);
                    OperandeRegistre reg = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeAdresseIndexee adr = (OperandeAdresseIndexee) instruction.getOperandes().get(1);
                    memoire.ecrireOctet(adresse++, (byte) reg.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresseBase() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresseBase() & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) adr.getRegistreIndex());
                }
                case STORE_INDEXE -> {
                    memoire.ecrireOctet(adresse++, (byte) 15);
                    OperandeRegistre reg = (OperandeRegistre) instruction.getOperandes().get(0);
                    OperandeAdresseIndexee adr = (OperandeAdresseIndexee) instruction.getOperandes().get(1);
                    memoire.ecrireOctet(adresse++, (byte) reg.getNumeroRegistre());
                    memoire.ecrireOctet(adresse++, (byte) ((adr.getAdresseBase() >> 8) & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) (adr.getAdresseBase() & 0xFF));
                    memoire.ecrireOctet(adresse++, (byte) adr.getRegistreIndex());
                }
            }
        }
    }

    /**
     * Décode l'instruction courante pointée par le compteur de programme.
     * Instancie l'instruction appropriée selon l'opcode lu.
     *
     * @return l'{@link Instruction} décodée
     * @throws IllegalStateException si le code d'opération est inconnu
     */
    public Instruction decoderInstruction(){
        byte opcode = lireOpocode();
        List<Operande> operandes = new ArrayList<>();

        return switch (opcode) {
            case 0 -> new Instruction(TypeInstruction.BREAK, operandes, "break");

            case 1 -> {
                int reg = lireOctetSuivant() & 0xFF;
                int val = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(reg));
                operandes.add(new OperandeConstante(val));
                yield  new Instruction(TypeInstruction.LOAD_CONSTANTE, operandes, "load r" + reg + ", " + val);
            }
            case 2 -> {
                int reg = lireOctetSuivant() & 0xFF;
                int adr = lireAdresseSuivante();
                operandes.add(new OperandeRegistre(reg));
                operandes.add(new OperandeAdresse(adr));
                yield new Instruction(TypeInstruction.LOAD_MEMOIRE, operandes, "load r" + reg + ", @" + adr);
            }
            case 3 -> {
                int reg = lireOctetSuivant() & 0xFF;
                int adr = lireAdresseSuivante();
                operandes.add(new OperandeRegistre(reg));
                operandes.add(new OperandeAdresse(adr));
                yield new Instruction(TypeInstruction.STORE_MEMOIRE, operandes, "store r" + reg + ", @" + adr);
            }
            case 4 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.ADD, operandes, "add r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 5 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.SUB, operandes, "sub r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 6 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.MUL, operandes, "mul r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 7 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.DIV, operandes, "div r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 8 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.AND, operandes, "and r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 9 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.OR, operandes, "or r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 10 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int rd = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeRegistre(rd));
                yield new Instruction(TypeInstruction.XOR, operandes, "xor r" + r1 + ", r" + r2 + ", r" + rd);
            }
            case 11 -> {
                int adr = lireAdresseSuivante();
                operandes.add(new OperandeAdresse(adr));
                yield new Instruction(TypeInstruction.JUMP, operandes, "jump @" + adr);
            }
            case 12 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int adr = lireAdresseSuivante();
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeAdresse(adr));
                yield new Instruction(TypeInstruction.BEQ, operandes, "beq r" + r1 + ", r" + r2 + ", @" + adr);
            }
            case 13 -> {
                int r1 = lireOctetSuivant() & 0xFF;
                int r2 = lireOctetSuivant() & 0xFF;
                int adr = lireAdresseSuivante();
                operandes.add(new OperandeRegistre(r1));
                operandes.add(new OperandeRegistre(r2));
                operandes.add(new OperandeAdresse(adr));
                yield new Instruction(TypeInstruction.BNE, operandes, "bne r" + r1 + ", r" + r2 + ", @" + adr);
            }
            case 14 -> {
                int reg = lireOctetSuivant() & 0xFF;
                int adrBase = lireAdresseSuivante();
                int regIndex = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(reg));
                operandes.add(new OperandeAdresseIndexee(adrBase, regIndex));
                yield new Instruction(TypeInstruction.LOAD_INDEXE, operandes, "load r" + reg + ", @" + adrBase + ", r" + regIndex);
            }
            case 15 -> {
                int reg = lireOctetSuivant() & 0xFF;
                int adrBase = lireAdresseSuivante();
                int regIndex = lireOctetSuivant() & 0xFF;
                operandes.add(new OperandeRegistre(reg));
                operandes.add(new OperandeAdresseIndexee(adrBase, regIndex));
                yield new Instruction(TypeInstruction.STORE_INDEXE, operandes, "store r" + reg + ", @" + adrBase + ", r" + regIndex);
            }
            default -> throw new IllegalStateException("Opcode inconnu: " + opcode);
        };
    }

    /**
     * Applique et exécute concrètement une instruction décodée
     * en modifiant l'état du processeur (registres, mémoire, PC, etc.).
     *
     * @param instruction l'{@link Instruction} à appliquer
     */
    public void appliquerInstruction(Instruction instruction){
        List<Operande> ops = instruction.getOperandes();
        switch (instruction.getTypeInstruction()) {
            case BREAK -> arreter();

            case LOAD_CONSTANTE -> {
                int reg = ((OperandeRegistre) ops.get(0)).getNumeroRegistre();
                byte val = (byte) ((OperandeConstante) ops.get(1)).getValeur();
                banqueRegistres.ecrireRegistre(reg, val);
            }
            case LOAD_MEMOIRE -> {
                int reg = ((OperandeRegistre) ops.get(0)).getNumeroRegistre();
                int adr = ((OperandeAdresse) ops.get(1)).getAdresse();
                byte val = memoire.lireOctet(adr);
                banqueRegistres.ecrireRegistre(reg, val);
            }
            case STORE_MEMOIRE -> {
                int reg = ((OperandeRegistre) ops.get(0)).getNumeroRegistre();
                int adr = ((OperandeAdresse) ops.get(1)).getAdresse();
                byte val = banqueRegistres.lireRegistre(reg);
                memoire.ecrireOctet(adr, val);
            }
            case ADD -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                banqueRegistres.ecrireRegistre(rd, alu.additionner(a, b));
            }
            case SUB -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                banqueRegistres.ecrireRegistre(rd, alu.soustraire(a, b));
            }
            case MUL -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                ResultatMulti res = alu.multiplier(a, b);
                banqueRegistres.ecrireRegistre(rd, res.getPoidsFaible());
                // poids fort perdu si un seul registre destination
            }
            case DIV -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                ResultatDiv res = alu.diviser(a, b);
                banqueRegistres.ecrireRegistre(rd, res.getQuotient());
                // reste perdu si un seul registre destination
            }
            case AND -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                banqueRegistres.ecrireRegistre(rd, alu.estBinaire(a, b));
            }
            case OR -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                banqueRegistres.ecrireRegistre(rd, alu.ouBinaire(a, b));
            }
            case XOR -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int rd = ((OperandeRegistre) ops.get(2)).getNumeroRegistre();
                banqueRegistres.ecrireRegistre(rd, alu.xorBinaire(a, b));
            }
            case JUMP -> {
                int adr = ((OperandeAdresse) ops.getFirst()).getAdresse();
                pc = adr; // override PC directly
            }
            case BEQ -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int adr = ((OperandeAdresse) ops.get(2)).getAdresse();
                if (a == b) pc = adr;
            }
            case BNE -> {
                byte a = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(0)).getNumeroRegistre());
                byte b = banqueRegistres.lireRegistre(((OperandeRegistre) ops.get(1)).getNumeroRegistre());
                int adr = ((OperandeAdresse) ops.get(2)).getAdresse();
                if (a != b) pc = adr;
            }
            case LOAD_INDEXE -> {
                int reg = ((OperandeRegistre) ops.get(0)).getNumeroRegistre();
                OperandeAdresseIndexee adr = (OperandeAdresseIndexee) ops.get(1);
                int adresseBase = adr.getAdresseBase();
                int index = banqueRegistres.lireRegistre(adr.getRegistreIndex()) & 0xFF;
                byte val = memoire.lireOctet((adresseBase + index) & 0xFFFF);
                banqueRegistres.ecrireRegistre(reg, val);
            }
            case STORE_INDEXE -> {
                int reg = ((OperandeRegistre) ops.get(0)).getNumeroRegistre();
                OperandeAdresseIndexee adr = (OperandeAdresseIndexee) ops.get(1);
                int adresseBase = adr.getAdresseBase();
                int index = banqueRegistres.lireRegistre(adr.getRegistreIndex()) & 0xFF;
                byte val = banqueRegistres.lireRegistre(reg);
                memoire.ecrireOctet((adresseBase + index) & 0xFFFF, val);
            }
        }
    }

    /**
     * Réinitialise complètement le processeur.
     * Vide la mémoire, efface tous les registres et remet le PC à zéro.
     */
    public void reinitialiser(){
        memoire.vider();
        banqueRegistres.reinitialiser();
        pc = 0;
        enExecution = false;
    }

    /**
     * Arrête l'exécution du programme en cours.
     */
    public void arreter(){
        enExecution = false;
    }
}
