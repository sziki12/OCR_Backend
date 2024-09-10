from Rect.WordRectangle import WordRectangle

class OcrDocument:
    def __init__(self, boxes, texts, probabilities):
        self.rows = []
        self.words = []

        for i in range(len(boxes)):
            self.words.insert(0, WordRectangle(boxes[i], texts[i], probabilities[i]))

        self.order_words_in_rows()

    def order_words_in_rows(self):
        print("BEGIN")
        for i in range(0, len(self.words)):
            for y in range(i, len(self.words)):
                same_row = self.words[i].is_in_same_line(self.words[y])
               # if same_row:
                print(self.words[i].text+" - "+ self.words[y].text+ ": "+ str(same_row))      
        print("END")            

