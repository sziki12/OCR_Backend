from Ocr.Rect.WordRectangle import WordRectangle, WordRelation
from Ocr.Rect.OcrRow import OcrRow

class OcrDocument:
    def __init__(self, boxes, texts, probabilities):
        self.rows = []
        self.words:list[WordRectangle] = []

        for i in range(len(boxes)):
            self.words.insert(0, WordRectangle(boxes[i], texts[i], probabilities[i]))

        self.order_words_in_rows()   

    def order_words_in_rows(self):
        for word in self.words:
            #print("\nword: "+word.text)
            if(len(self.rows)==0):
                self.rows.append(OcrRow(word))
                #print("self.rows.append(OcrRow(word)")
            else:
                has_row = False
                for row_index in range(len(self.rows)):
                    #print("row_index: "+str(row_index))
                    relative_place = self.rows[row_index].is_in_row(word)
                    if(relative_place == WordRelation.SAME):
                        #print(word.text+ " SAME "+self.rows[row_index].get_text())
                        self.rows[row_index].add_word(word)
                        has_row = True
                        break
                    elif(relative_place == WordRelation.ABOVE):
                        self.rows.insert(row_index, OcrRow(word))
                        has_row = True
                        #print(word.text+ " ABOVE "+self.rows[row_index].get_text())
                        break
                    #else:
                        #print(word.text+ " BELOW "+self.rows[row_index].get_text())
                if has_row == False:
                     self.rows.insert(0, OcrRow(word))  
                #TODO Fix duplicated entity error, word order and word row prediction
        print(self.get_text())               

    def get_text(self):
        text = ""
        for row in self.rows:
            text += row.get_text()+"\n"
        return text                      

