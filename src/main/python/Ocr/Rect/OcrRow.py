from Ocr.Rect.WordRectangle import WordRectangle, WordRelation
class OcrRow:
    def __init__(self, first_word:str) :
        self.words = []
        self.words.append(first_word)

    def is_in_row(self, new_word:WordRectangle):
        row_marker = self.words[0]
        if(row_marker.is_in_same_line(new_word) or new_word.is_in_same_line(row_marker)):
            return WordRelation.SAME
        
        elif (row_marker.middle_point[1] < new_word.middle_point[1]):
            return WordRelation.ABOVE
        
        elif (row_marker.middle_point[1] > new_word.middle_point[1]):
            return WordRelation.BELOW

    def add_word(self, new_word:WordRectangle):    
        place_found  = False
        for i in range(len(self.words)):
            relation = new_word.order(self.words[i])
            """print("new: "+str(new_word.text))
            print("i: "+str(self.words[i].text))
            print(relation)"""
            if relation == WordRelation.LEFT:
                self.words.insert(i, new_word)
                place_found = True
                break

        if not place_found:
            self.words.insert(len(self.words), new_word)

    def get_text(self):
        text = ""
        for word_index in range(len(self.words)):
            if word_index == 0:
                text+=self.words[word_index].text
            else:
                text+=" "+self.words[word_index].text
        return text