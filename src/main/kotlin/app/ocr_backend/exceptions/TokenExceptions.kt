package app.ocr_backend.exceptions

class UsedTokenException(token:String):Exception("Token: $token was already used")

class InvalidTokenException(token:String):Exception("Token: $token is not a valid token")