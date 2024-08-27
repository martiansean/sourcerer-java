const express = require('express');
const Anthropic = require('@anthropic-ai/sdk');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


console.log(process.env.ANTROPIC_KEY);
const anthropic = new Anthropic({
    apiKey: process.env.ANTROPIC_KEY,
});


function ExtractTags(string) {
    return;
}

app.post('/generate', async (req, res) => {

    const prompt = "";

    const msg = await anthropic.messages.create({
        model: "claude-3-5-sonnet-20240620",
        max_tokens: 1024,
        messages: [{ role: "user", content: req.body.content }],
    });

    let response = msg.content[0].text;
    
    res.json(response);
    
});

app.listen(4000, () => {
    console.log('Server is running on port 3000');
});


