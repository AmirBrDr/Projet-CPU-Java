package assembleur;

/**
 * Définit les différents types d'instructions gérées par le processeur.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public enum TypeInstruction {
    LOAD_CONSTANTE,
    LOAD_MEMOIRE,
    LOAD_INDEXE,
    STORE_MEMOIRE,
    STORE_INDEXE,
    BREAK,
    ADD,
    SUB,
    MUL,
    DIV,
    AND,
    OR,
    XOR,
    JUMP,
    BEQ,
    BNE,
    DATA,
    STRING
}
