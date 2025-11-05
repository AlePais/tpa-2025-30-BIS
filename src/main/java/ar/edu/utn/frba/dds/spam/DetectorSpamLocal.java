package ar.edu.utn.frba.dds.spam;

/*import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.util.BytesRef;*/

import java.io.IOException;

public class DetectorSpamLocal implements DetectorDeSpam {

  @Override
  public boolean esSpam(String texto) {
    return false;
    /*String[] corpus = {
        "Compra ahora y gana dinero fácil",         // spam
        "Haz clic aquí para recibir tu premio",     // spam
        "Hola Juan, te envío el informe",           // no spam
        "Recordatorio de la reunión del lunes",     // no spam
    };

    Analyzer analyzer = new StandardAnalyzer();
    Directory directory = new ByteBuffersDirectory();

    try {
      // Indexamos el corpus
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      try (IndexWriter writer = new IndexWriter(directory, config)) {
        for (int i = 0; i < corpus.length; i++) {
          Document doc = new Document();
          doc.add(new TextField("contenido", corpus[i], Field.Store.YES));
          doc.add(new StringField("tipo", (i < 2) ? "spam" : "ham", Field.Store.YES));
          writer.addDocument(doc);
        }
      }

      try (DirectoryReader reader = DirectoryReader.open(directory)) {
        Terms terms = MultiTerms.getTerms(reader, "contenido");
        if (terms == null) {
          return false;
        }

        TermsEnum termsEnum = terms.iterator();
        BytesRef term;
        int numDocs = reader.numDocs();
        double score = 0.0;

        while ((term = termsEnum.next()) != null) {
          String palabra = term.utf8ToString();

          if (texto.toLowerCase().contains(palabra)) {
            int docFreq = termsEnum.docFreq();
            double idf = Math.log((double) numDocs / (docFreq + 1));

            PostingsEnum docsEnum = MultiTerms.getTermPostingsEnum(reader, "contenido", term);
            int tf = 0;
            if (docsEnum != null) {
              while (docsEnum.nextDoc() != PostingsEnum.NO_MORE_DOCS) {
                tf += docsEnum.freq();
              }
            }

            double tfidf = tf * idf;
            score += tfidf;
          }
        }

        // Umbral simple (ajustable)
        return score > 1.5;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }*/
  }
}